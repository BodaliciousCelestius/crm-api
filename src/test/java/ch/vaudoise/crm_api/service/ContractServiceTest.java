package ch.vaudoise.crm_api.service;

import static ch.vaudoise.crm_api.fixtures.ClientFixture.aClient;
import static ch.vaudoise.crm_api.fixtures.ContractFixture.aContract;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.model.entity.Client;
import ch.vaudoise.crm_api.model.entity.Contract;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import ch.vaudoise.crm_api.repository.ClientRepository;
import ch.vaudoise.crm_api.repository.ContractRepository;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ContractServiceTest {

  @InjectMocks private ContractService contractService;

  @Mock private ContractRepository contractRepository;

  @Mock private ClientRepository clientRepository;

  @Nested
  class Create {
    @Test
    void shouldCreateContractForExistingClient() {
      Contract mockContract = aContract();
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();

      CreateContractDTO dto =
          new CreateContractDTO(LocalDate.now(), LocalDate.now().plusDays(10), new Decimal128(500));

      when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));
      when(contractRepository.save(any(Contract.class))).thenReturn(Mono.just(mockContract));

      StepVerifier.create(contractService.create(clientId.toHexString(), dto))
          .expectNext(mockContract.getId().toString())
          .verifyComplete();

      verify(clientRepository).findById(clientId);
      verify(contractRepository).save(any(Contract.class));
    }

    @Test
    void shouldFailWhenClientNotFound() {
      ObjectId clientId = aClient().getId();

      CreateContractDTO dto =
          new CreateContractDTO(LocalDate.now(), LocalDate.now().plusDays(10), new Decimal128(500));

      when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

      StepVerifier.create(contractService.create(clientId.toHexString(), dto))
          .expectError(NotFoundException.class)
          .verify();

      verify(clientRepository).findById(clientId);
      verify(contractRepository, never()).save(any());
    }

    @Test
    void shouldDefaultStartDateWhenNull() {
      Contract mockContract = aContract();
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();

      CreateContractDTO dto =
          new CreateContractDTO(null, LocalDate.now().plusDays(30), new Decimal128(500));

      when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));
      when(contractRepository.save(any(Contract.class))).thenReturn(Mono.just(mockContract));

      StepVerifier.create(contractService.create(clientId.toHexString(), dto))
          .expectNext(mockContract.getId().toString())
          .verifyComplete();

      verify(contractRepository)
          .save(
              argThat(c -> c.getStartDate() != null && c.getClientId().equals(mockClient.getId())));
    }
  }

  @Nested
  class Update {
    @Test
    void shouldUpdateExistingContract() {
      Contract mockContract = aContract();
      UpdateContractDTO dto =
          new UpdateContractDTO(LocalDate.now(), LocalDate.now().plusDays(1), new Decimal128(1));

      when(contractRepository.findById(mockContract.getId())).thenReturn(Mono.just(mockContract));
      when(contractRepository.save(any(Contract.class))).thenReturn(Mono.just(mockContract));

      StepVerifier.create(contractService.update(mockContract.getId().toHexString(), dto))
          .verifyComplete();

      verify(contractRepository).findById(mockContract.getId());
      verify(contractRepository).save(any(Contract.class));
    }

      @Test
      void shouldUpdateExistingContractWithNullFields() {
          Contract mockContract = aContract();
          UpdateContractDTO dto =
                  new UpdateContractDTO(null, null, new Decimal128(1));

          when(contractRepository.findById(mockContract.getId())).thenReturn(Mono.just(mockContract));
          when(contractRepository.save(any(Contract.class))).thenReturn(Mono.just(mockContract));

          StepVerifier.create(contractService.update(mockContract.getId().toHexString(), dto))
                  .verifyComplete();

          verify(contractRepository).findById(mockContract.getId());
          verify(contractRepository).save(any(Contract.class));
      }

    @Test
    void shouldErrorWhenNotFound() {
      Contract mockContract = aContract();

      when(contractRepository.findById(mockContract.getId())).thenReturn(Mono.empty());

      StepVerifier.create(
              contractService.update(
                  mockContract.getId().toHexString(), new UpdateContractDTO(null, null, null)))
          .expectError(NotFoundException.class)
          .verify();

      verify(contractRepository).findById(mockContract.getId());
      verify(contractRepository, never()).save(any());
    }
  }

  @Nested
  class Delete {
    @Test
    void shouldDeleteExistingContract() {
      Contract mockContract = aContract();
      ObjectId contractId = mockContract.getId();
      when(contractRepository.findById(contractId)).thenReturn(Mono.just(mockContract));
      when(contractRepository.delete(mockContract)).thenReturn(Mono.empty());

      StepVerifier.create(contractService.delete(contractId.toHexString())).verifyComplete();

      verify(contractRepository).findById(contractId);
      verify(contractRepository).delete(mockContract);
    }

    @Test
    void shouldErrorWhenContractNotFound() {
      Contract mockContract = aContract();
      ObjectId contractId = mockContract.getId();
      when(contractRepository.findById(contractId)).thenReturn(Mono.empty());

      StepVerifier.create(contractService.delete(contractId.toHexString()))
          .expectError(NotFoundException.class)
          .verify();

      verify(contractRepository).findById(contractId);
      verify(contractRepository, never()).delete(any());
    }
  }
}
