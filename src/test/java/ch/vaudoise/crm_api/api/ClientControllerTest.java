package ch.vaudoise.crm_api.api;

import static ch.vaudoise.crm_api.fixtures.ClientFixture.*;
import static ch.vaudoise.crm_api.fixtures.ContractFixture.aResponseContractDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ch.vaudoise.crm_api.api.controller.ClientController;
import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import ch.vaudoise.crm_api.service.ClientService;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ClientController.class)
class ClientControllerTest {

  @Autowired private WebTestClient webTestClient;

  @MockitoBean private ClientService clientService;

  @Nested
  class Get {
    @Test
    void testGetClientShouldReturn200() {
      ResponseClientDTO response = aResponseClientDTO();

      when(clientService.findById("1")).thenReturn(Mono.just(response));

      webTestClient
          .get()
          .uri("/api/clients/1")
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(ResponseClientDTO.class)
          .isEqualTo(response);
    }
  }

  @Nested
  class GetAllContractsForClient {
    @Test
    void testGetAllActiveContractsShouldReturn200() {
      ResponseContractDTO contractDTO = aResponseContractDTO();

      when(clientService.getAllActiveContracts(contractDTO.client().id(), null, null))
          .thenReturn(Flux.just(contractDTO));

      webTestClient
          .get()
          .uri("/api/clients/" + contractDTO.client().id() + "/contracts")
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBodyList(ResponseContractDTO.class)
          .hasSize(1)
          .contains(contractDTO);
    }

    @Test
    void testGetAllActiveContractsWithFilterShouldReturn200() {
      ResponseContractDTO contractDTO = aResponseContractDTO();

      when(clientService.getAllActiveContracts(
              eq(contractDTO.id()), any(LocalDate.class), any(LocalDate.class)))
          .thenReturn(Flux.just(contractDTO));

      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/api/clients/" + contractDTO.client().id() + "/contracts")
                      .queryParam("from", LocalDate.now().minusYears(3))
                      .queryParam("to", LocalDate.now())
                      .build())
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBodyList(ResponseContractDTO.class)
          .hasSize(1)
          .contains(contractDTO);
    }

    @Test
    void testGetAllActiveContractsWithInvalidFilterShouldReturn400() {
      ResponseContractDTO contractDTO = aResponseContractDTO();

      when(clientService.getAllActiveContracts(
              eq(contractDTO.id()), any(LocalDate.class), any(LocalDate.class)))
          .thenReturn(Flux.just(contractDTO));

      webTestClient
          .get()
          .uri(
              uriBuilder ->
                  uriBuilder
                      .path("/api/clients/" + contractDTO.client().id() + "/contracts")
                      .queryParam("from", LocalDate.now().minusYears(3))
                      .queryParam("to", "201611-11")
                      .build())
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }
  }

  @Nested
  class ContractsTotalSum {
    @Test
    void testGetAllActiveContractsTotalSumShouldReturn200() {

      when(clientService.getAllActiveContractsTotalSum(aResponseContractDTO().client().id()))
          .thenReturn(Mono.just(Decimal128.parse("5.2")));

      webTestClient
          .get()
          .uri("/api/clients/" + aResponseContractDTO().client().id() + "/contracts/total")
          .accept(MediaType.APPLICATION_JSON)
          .exchange()
          .expectStatus()
          .isOk()
          .expectBody(BigDecimal.class)
          .isEqualTo(BigDecimal.valueOf(5.2));
    }
  }

  @Nested
  class Create {
    @Test
    void testCreateClientShouldReturn201() {
      when(clientService.create(any(CreateClientDTO.class))).thenReturn(Mono.empty());

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(aCreateClientDTO())
          .exchange()
          .expectStatus()
          .isCreated();
    }

    @Test
    void testInvalidTypeShouldReturn400() {
      String invalidJson =
          """
                    {
                        "type": "invalid",
                        "name": "John Doe",
                        "phone": "+1492910029394",
                        "email": "john.doe@anonymous.org",
                        "birthday": "1975-02-02"
                    }
                    """;

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidJson)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testInvalidNameShouldReturn400() {
      CreateClientDTO invalidNameDTO = aCreateClientDTO().toBuilder().name("").build();

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidNameDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testInvalidEmailShouldReturn400() {
      CreateClientDTO invalidEmailDTO =
          aCreateClientDTO().toBuilder().email("invalid-email").build();

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidEmailDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testInvalidPhoneShouldReturn400() {
      CreateClientDTO invalidPhoneDTO = aCreateClientDTO().toBuilder().phone("invalid").build();
      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidPhoneDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testMissingRequiredFieldShouldReturn400() {
      CreateClientDTO missingNameDTO = aCreateClientDTO().toBuilder().name(null).build();

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(missingNameDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testBirthdayShouldBeCorrectlyFormatted() {
      String invalidJson =
          """
                          {
                              "type": "PERSON",
                              "name": "John Doe",
                              "phone": "+1492910029394",
                              "email": "john.doe@anonymous.org",
                              "birthday": "197502-02"
                          }
                          """;

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidJson)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testBirthdayInFutureShouldReturn400() {
      CreateClientDTO invalidBirthDateDTO =
          aCreateClientDTO().toBuilder().birthday(LocalDate.now().plusMonths(1)).build();

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidBirthDateDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testInvalidCompanyBirthdayCompanyIdentifierPairShouldReturn400() {
      CreateClientDTO invalidBirthDateDTO =
          aCreateClientDTO().toBuilder()
              .type(ClientType.COMPANY)
              .birthday(LocalDate.now().minusDays(1))
              .companyIdentifier(null)
              .build();

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidBirthDateDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testInvalidPersonBirthdayCompanyIdentifierPairShouldReturn400() {
      CreateClientDTO invalidBirthdayDTO =
          aCreateClientDTO().toBuilder()
              .type(ClientType.PERSON)
              .birthday(null)
              .companyIdentifier("company-id")
              .build();

      webTestClient
          .post()
          .uri("/api/clients")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidBirthdayDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }
  }

  @Nested
  class Update {
    @Test
    void testUpdateClientShouldReturn200() {
      when(clientService.update(eq("1"), any(UpdateClientDTO.class))).thenReturn(Mono.empty());

      webTestClient
          .put()
          .uri("/api/clients/1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(anUpdateClientDTO())
          .exchange()
          .expectStatus()
          .isOk();
    }

    @Test
    void testInvalidEmailShouldReturn400() {
      UpdateClientDTO invalidEmailUpdate =
          anUpdateClientDTO().toBuilder().email("invalid-email").build();

      webTestClient
          .put()
          .uri("/api/clients/1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidEmailUpdate)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testInvalidPhoneShouldReturn400() {
      UpdateClientDTO invalidPhoneUpdate = anUpdateClientDTO().toBuilder().phone("invalid").build();

      webTestClient
          .put()
          .uri("/api/clients/1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidPhoneUpdate)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }
  }

  @Nested
  class Delete {
    @Test
    void testDeleteClientShouldReturn204() {
      when(clientService.delete("1")).thenReturn(Mono.empty());

      webTestClient.delete().uri("/api/clients/1").exchange().expectStatus().isNoContent();
    }
  }
}
