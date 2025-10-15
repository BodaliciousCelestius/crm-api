package ch.vaudoise.crm_api.service;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.model.entity.Contract;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import ch.vaudoise.crm_api.repository.ClientRepository;
import ch.vaudoise.crm_api.repository.ContractRepository;
import java.time.Instant;
import java.time.LocalDate;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ContractService {

  private final ClientRepository clientRepository;
  private final ContractRepository contractRepository;

  public ContractService(
      final ClientRepository clientRepository, final ContractRepository contractRepository) {
    this.clientRepository = clientRepository;
    this.contractRepository = contractRepository;
  }

  public Mono<String> create(final String clientId, final CreateContractDTO dto) {
    return clientRepository
        .findById(new ObjectId(clientId))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + clientId)))
        .flatMap(
            client -> {
              Contract contract =
                  Contract.builder()
                      .startDate(dto.startDate() == null ? LocalDate.now() : dto.startDate())
                      .endDate(dto.endDate())
                      .cost(dto.cost())
                      .clientId(new ObjectId(clientId))
                      .updatedAt(Instant.now())
                      .build();
              return contractRepository.save(contract);
            })
        .map(contract -> contract.getId().toString());
  }

  public Mono<Void> update(final String id, final UpdateContractDTO dto) {
    return contractRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Contract not found: " + id)))
        .flatMap(
            contract -> {
              Contract.ContractBuilder updateContract = contract.toBuilder();
              if (dto.startDate() != null) updateContract.startDate(dto.startDate());
              if (dto.endDate() != null) updateContract.endDate(dto.endDate());
              if (dto.cost() != null) updateContract.cost(dto.cost());
              updateContract.updatedAt(Instant.now());
              return contractRepository.save(updateContract.build());
            })
        .then();
  }

  public Mono<Void> delete(final String id) {
    return contractRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Contract not found: " + id)))
        .flatMap(contractRepository::delete);
  }
}
