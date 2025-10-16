package ch.vaudoise.crm_api.service;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.model.entity.Contract;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import ch.vaudoise.crm_api.repository.ClientRepository;
import ch.vaudoise.crm_api.repository.ContractRepository;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.time.Instant;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP2"},
    justification = "Ignore warning on Spring Boot Dependency Injection EI_EXPOSE_REP2")
@Slf4j
@Service
public class ContractService {

  private final ClientRepository clientRepository;
  private final ContractRepository contractRepository;

  public ContractService(
      final ClientRepository clientRepository, final ContractRepository contractRepository) {
    this.clientRepository = clientRepository;
    this.contractRepository = contractRepository;
  }

  @CacheEvict(value = "contracts", allEntries = true)
  public Mono<String> create(final String clientId, final CreateContractDTO dto) {
    log.info(
        "Creating new contract for client {} : startDate={}, endDate={}, cost={}",
        clientId,
        dto.startDate(),
        dto.endDate(),
        dto.cost());
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
        .map(contract -> contract.getId().toString())
        .doOnSuccess(
            id -> log.info("Contract successfully created id={} for clientId={}", id, clientId))
        .doOnError(
            e ->
                log.error(
                    "Failed to create contract for clientId={}: {}", clientId, e.getMessage()));
  }

  @CacheEvict(value = "contracts", allEntries = true)
  public Mono<Void> update(final String id, final UpdateContractDTO dto) {
    log.info(
        "Updating contract id={} : startDate={}, endDate={}, cost={}",
        id,
        dto.startDate(),
        dto.endDate(),
        dto.cost());

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
        .doOnSuccess(v -> log.info("Contract successfully updated: id={}", id))
        .doOnError(e -> log.error("Error while updating contract {}: {}", id, e.getMessage()))
        .then();
  }

  public Mono<Void> delete(final String id) {
    log.info("Deleting contract: id={}", id);
    return contractRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Contract not found: " + id)))
        .flatMap(contractRepository::delete)
        .doOnSuccess(v -> log.info("Contract successfully deleted: id={}", id))
        .doOnError(e -> log.error("Error while deleting contract {}: {}", id, e.getMessage(), e));
  }
}
