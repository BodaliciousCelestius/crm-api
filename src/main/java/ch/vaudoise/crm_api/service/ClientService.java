package ch.vaudoise.crm_api.service;

import static java.time.ZoneOffset.UTC;

import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import ch.vaudoise.crm_api.model.entity.Client;
import ch.vaudoise.crm_api.model.entity.Contract;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import ch.vaudoise.crm_api.repository.ClientRepository;
import ch.vaudoise.crm_api.repository.ContractRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientService {

  private final ClientRepository clientRepository;
  private final ContractRepository contractRepository;

  @SuppressFBWarnings
  public ClientService(ClientRepository clientRepository, ContractRepository contractRepository) {
    this.clientRepository = clientRepository;
    this.contractRepository = contractRepository;
  }

  public Mono<ResponseClientDTO> findById(String id) {
    return clientRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .map(Client::toDTO);
  }

  public Flux<ResponseContractDTO> getAllActiveContracts(String id, LocalDate from, LocalDate to) {
    ObjectId objectId = new ObjectId(id);
    Instant fromInstant = from == null ? null : from.atStartOfDay().toInstant(UTC);
    Instant toInstant =
        to == null ? null : to.plusDays(1).atStartOfDay(UTC).toInstant().minusSeconds(1);

    return clientRepository
        .findById(objectId)
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .flatMapMany(
            client -> {
              LocalDate today = LocalDate.now();
              Flux<Contract> contracts;

              if (fromInstant != null && toInstant != null) {
                if (fromInstant.isAfter(toInstant)) {
                  return Flux.error(
                      new IllegalArgumentException(
                          "Parameter 'from' and 'to' must be chronologically coherent (from < to)."));
                }
                contracts =
                    contractRepository.findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtBetween(
                        objectId, today, fromInstant, toInstant);
              } else if (fromInstant == null && toInstant != null) {
                contracts =
                    contractRepository
                        .findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtLessThanEqual(
                            objectId, today, toInstant);
              } else if (fromInstant != null) {
                contracts =
                    contractRepository
                        .findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtGreaterThanEqual(
                            objectId, today, fromInstant);
              } else {
                contracts =
                    contractRepository.findByClientIdAndEndDateGreaterThanEqual(objectId, today);
              }

              return contracts.map(contract -> contract.toDTO(client.toDTO()));
            });
  }

  public Mono<Decimal128> getAllActiveContractsTotalSum(String id) {
    return clientRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .flatMap(
            client ->
                contractRepository.sumActiveContractTotalCostByClientId(
                    new ObjectId(id), LocalDate.now()))
        .switchIfEmpty(Mono.just(new Decimal128(0)));
  }

  public Mono<String> create(CreateClientDTO dto) {
    Client client =
        Client.builder()
            .type(dto.type())
            .name(dto.name())
            .phone(dto.phone())
            .email(dto.email())
            .birthday(dto.birthday())
            .companyIdentifier(dto.companyIdentifier())
            .build();

    return clientRepository.save(client).map(c -> c.getId().toString());
  }

  public Mono<Void> update(String id, UpdateClientDTO dto) {
    return clientRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .flatMap(
            client -> {
              Client.ClientBuilder updateClient = client.toBuilder();
              if (dto.name() != null) updateClient.name(dto.name());
              if (dto.type() != null) updateClient.type(dto.type());
              if (dto.email() != null) updateClient.email(dto.email());
              if (dto.phone() != null) updateClient.phone(dto.phone());
              return clientRepository.save(updateClient.build());
            })
        .then();
  }

  public Mono<Void> delete(String id) {
    return clientRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .flatMap(clientRepository::delete)
        .then(contractRepository.unsetClientIdByClientId(new ObjectId(id)))
        .then(contractRepository.setEndDateByClientId(new ObjectId(id), LocalDate.now()));
  }
}
