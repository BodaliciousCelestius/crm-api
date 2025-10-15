package ch.vaudoise.crm_api.service;

import static ch.vaudoise.crm_api.fixtures.ClientFixture.*;
import static ch.vaudoise.crm_api.fixtures.ContractFixture.aContract;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;

import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.model.entity.Client;
import ch.vaudoise.crm_api.model.entity.Contract;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import ch.vaudoise.crm_api.repository.ClientRepository;
import ch.vaudoise.crm_api.repository.ContractRepository;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
class ClientServiceTest {

  @Autowired private ClientService clientService;

  @MockitoBean private ClientRepository clientRepository;

  @MockitoBean private ContractRepository contractRepository;

  @Nested
  class FindById {

    @Test
    void shouldReturnClient() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));

      StepVerifier.create(clientService.findById(clientId.toString()))
          .expectNextMatches(dto -> dto.equals(mockClient.toDTO()))
          .verifyComplete();
    }

    @Test
    void shouldThrowWhenNotFound() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

      StepVerifier.create(clientService.findById(clientId.toString()))
          .expectErrorSatisfies(
              err ->
                  assertThat(err)
                      .isInstanceOf(NotFoundException.class)
                      .hasMessageContaining("Client not found"))
          .verify();
    }
  }

  @Nested
  class GetAllActiveContracts {

    @Test
    void shouldFetchContractsBetweenFromAndTo() {
      Client mockClient = aClient();
      Contract mockContract = aContract();

      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(
              contractRepository.findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtBetween(
                  eq(mockClient.getId()), any(), any(), any()))
          .thenReturn(Flux.just(mockContract));

      StepVerifier.create(
              clientService.getAllActiveContracts(
                  mockClient.getId().toString(), LocalDate.now().minusDays(5), LocalDate.now()))
          .expectNextMatches(dto -> dto.equals(mockContract.toDTO(mockClient.toDTO())))
          .verifyComplete();
    }

    @Test
    void shouldFetchContractBetweenNullAndNull() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Contract mockContract = aContract();

      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));
      Mockito.when(
              contractRepository.findByClientIdAndEndDateGreaterThanEqual(
                  eq(mockClient.getId()), any()))
          .thenReturn(Flux.just(mockContract));

      StepVerifier.create(
              clientService.getAllActiveContracts(mockClient.getId().toString(), null, null))
          .expectNextMatches(dto -> dto.equals(mockContract.toDTO(mockClient.toDTO())))
          .verifyComplete();
    }

    @Test
    void shouldFetchContractBetweenFromAndNull() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Contract mockContract = aContract();

      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));
      Mockito.when(
              contractRepository
                  .findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtGreaterThanEqual(
                      eq(mockClient.getId()), any(), any()))
          .thenReturn(Flux.just(mockContract));

      StepVerifier.create(
              clientService.getAllActiveContracts(
                  mockClient.getId().toString(), LocalDate.now(), null))
          .expectNextMatches(dto -> dto.equals(mockContract.toDTO(mockClient.toDTO())))
          .verifyComplete();
    }

    @Test
    void shouldFetchContractBetweenNullAndTo() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Contract mockContract = aContract();

      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));
      Mockito.when(
              contractRepository.findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtLessThanEqual(
                  eq(mockClient.getId()), any(), any()))
          .thenReturn(Flux.just(mockContract));

      StepVerifier.create(
              clientService.getAllActiveContracts(
                  mockClient.getId().toString(), null, LocalDate.now()))
          .expectNextMatches(dto -> dto.equals(mockContract.toDTO(mockClient.toDTO())))
          .verifyComplete();
    }

    @Test
    void shouldThrowWhenNotFound() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

      StepVerifier.create(clientService.getAllActiveContracts(clientId.toString(), null, null))
          .expectErrorSatisfies(
              err ->
                  assertThat(err)
                      .isInstanceOf(NotFoundException.class)
                      .hasMessageContaining("Client not found"))
          .verify();
    }

    @Test
    void shouldThrowIfFromAfterTo() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.just(mockClient));

      StepVerifier.create(
              clientService.getAllActiveContracts(
                  clientId.toString(), LocalDate.now(), LocalDate.now().minusDays(2)))
          .expectError(IllegalArgumentException.class)
          .verify();
    }
  }

  @Nested
  class GetAllActiveContractsTotalSum {

    @Test
    void shouldReturnSumOfActiveContracts() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(contractRepository.sumActiveContractTotalCostByClientId(any(), any()))
          .thenReturn(Mono.just(new Decimal128(500)));

      StepVerifier.create(
              clientService.getAllActiveContractsTotalSum(mockClient.getId().toString()))
          .expectNext(new Decimal128(500))
          .verifyComplete();
    }

    @Test
    void shouldThrowWhenNotFound() {
      Client mockClient = aClient();
      ObjectId clientId = mockClient.getId();
      Mockito.when(clientRepository.findById(clientId)).thenReturn(Mono.empty());

      StepVerifier.create(clientService.getAllActiveContractsTotalSum(clientId.toString()))
          .expectErrorSatisfies(
              err ->
                  assertThat(err)
                      .isInstanceOf(NotFoundException.class)
                      .hasMessageContaining("Client not found"))
          .verify();
    }

    @Test
    void shouldReturnZeroIfNone() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(contractRepository.sumActiveContractTotalCostByClientId(any(), any()))
          .thenReturn(Mono.empty());

      StepVerifier.create(
              clientService.getAllActiveContractsTotalSum(mockClient.getId().toString()))
          .expectNext(new Decimal128(0))
          .verifyComplete();
    }
  }

  @Nested
  class CreateClient {

    @Test
    void shouldSaveClient() {

      Client mockClient = aClient();
      CreateClientDTO dto =
          CreateClientDTO.builder()
              .name(mockClient.getName())
              .type(mockClient.getType())
              .email(mockClient.getEmail())
              .phone(mockClient.getPhone())
              .birthday(mockClient.getBirthday())
              .companyIdentifier(mockClient.getCompanyIdentifier())
              .build();

      Mockito.when(clientRepository.save(any(Client.class))).thenReturn(Mono.just(mockClient));

      StepVerifier.create(clientService.create(dto))
          .expectNext(mockClient.getId().toString())
          .verifyComplete();

      ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
      Mockito.verify(clientRepository).save(captor.capture());
      assertThat(captor.getValue())
          .usingRecursiveComparison()
          .ignoringActualNullFields()
          .isEqualTo(mockClient);
    }
  }

  @Nested
  class UpdateClient {

    @Test
    void shouldUpdateClient() {
      Client mockClient = aClient();
      UpdateClientDTO dto =
          UpdateClientDTO.builder()
              .name(mockClient.getName())
              .type(mockClient.getType())
              .email(mockClient.getEmail())
              .phone(mockClient.getPhone())
              .build();

      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(clientRepository.save(any(Client.class))).thenReturn(Mono.just(mockClient));

      StepVerifier.create(clientService.update(mockClient.getId().toString(), dto))
          .verifyComplete();

      ArgumentCaptor<Client> captor = ArgumentCaptor.forClass(Client.class);
      Mockito.verify(clientRepository).save(captor.capture());
      assertThat(captor.getValue())
          .usingRecursiveComparison()
          .ignoringActualNullFields()
          .isEqualTo(mockClient);
    }

    @Test
    void shouldThrowWhenNotFound() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.empty());

      StepVerifier.create(
              clientService.update(
                  mockClient.getId().toString(), new UpdateClientDTO(null, null, null, null)))
          .expectError(NotFoundException.class)
          .verify();
    }
  }

  @Nested
  class DeleteClient {

    @Test
    void shouldDeleteClient() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(clientRepository.delete(mockClient)).thenReturn(Mono.empty());
      Mockito.when(contractRepository.unsetClientIdByClientId(mockClient.getId()))
          .thenReturn(Mono.empty());
      Mockito.when(contractRepository.setEndDateByClientId(eq(mockClient.getId()), any()))
          .thenReturn(Mono.empty());

      StepVerifier.create(clientService.delete(mockClient.getId().toString())).verifyComplete();
      Mockito.verify(clientRepository).delete(mockClient);
    }

    @Test
    void shouldUpdateEndDateUponDeletion() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(clientRepository.delete(mockClient)).thenReturn(Mono.empty());
      Mockito.when(contractRepository.unsetClientIdByClientId(mockClient.getId()))
          .thenReturn(Mono.empty());
      Mockito.when(contractRepository.setEndDateByClientId(eq(mockClient.getId()), any()))
          .thenReturn(Mono.empty());

      StepVerifier.create(clientService.delete(mockClient.getId().toString())).verifyComplete();
      Mockito.verify(contractRepository).setEndDateByClientId(mockClient.getId(), LocalDate.now());
    }

    @Test
    void shouldUnsetAllIdsByClient() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.just(mockClient));
      Mockito.when(clientRepository.delete(mockClient)).thenReturn(Mono.empty());
      Mockito.when(contractRepository.unsetClientIdByClientId(mockClient.getId()))
          .thenReturn(Mono.empty());
      Mockito.when(contractRepository.setEndDateByClientId(eq(mockClient.getId()), any()))
          .thenReturn(Mono.empty());

      StepVerifier.create(clientService.delete(mockClient.getId().toString())).verifyComplete();
      Mockito.verify(contractRepository).unsetClientIdByClientId(mockClient.getId());
    }

    @Test
    void shouldThrowWhenNotFound() {
      Client mockClient = aClient();
      Mockito.when(clientRepository.findById(mockClient.getId())).thenReturn(Mono.empty());
      Mockito.when(clientRepository.delete(mockClient)).thenReturn(Mono.empty());
      Mockito.when(contractRepository.unsetClientIdByClientId(mockClient.getId()))
          .thenReturn(Mono.empty());
      Mockito.when(contractRepository.setEndDateByClientId(eq(mockClient.getId()), any()))
          .thenReturn(Mono.empty());

      StepVerifier.create(clientService.delete(mockClient.getId().toString()))
          .expectError(NotFoundException.class)
          .verify();
    }
  }
}
