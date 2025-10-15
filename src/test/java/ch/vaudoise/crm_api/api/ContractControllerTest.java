package ch.vaudoise.crm_api.api;

import static ch.vaudoise.crm_api.fixtures.ContractFixture.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import ch.vaudoise.crm_api.api.controller.ContractController;
import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.service.ContractService;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(controllers = ContractController.class)
class ContractControllerTest {

  @Autowired private WebTestClient webTestClient;

  @MockitoBean private ContractService contractService;

  @Nested
  class Create {
    @Test
    void testCreateContractShouldReturn201() {
      when(contractService.create(eq("1"), any(CreateContractDTO.class))).thenReturn(Mono.empty());

      webTestClient
          .post()
          .uri(uriBuilder -> uriBuilder.path("/api/contracts").queryParam("clientId", "1").build())
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(aCreateContractDTO())
          .exchange()
          .expectStatus()
          .isCreated();
    }

    @Test
    void testMissingRequiredFieldShouldReturn400() {
      CreateContractDTO missingCostDTO = aCreateContractDTO().toBuilder().cost(null).build();

      webTestClient
          .post()
          .uri("/api/contracts")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(missingCostDTO)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }

    @Test
    void testNegativeCostShouldReturn400() {
      CreateContractDTO invalidCost =
          new CreateContractDTO(LocalDate.now(), null, new Decimal128(-10));

      webTestClient
          .post()
          .uri(uriBuilder -> uriBuilder.path("/api/contracts").queryParam("clientId", "1").build())
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidCost)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }
  }

  @Nested
  class Update {
    @Test
    void testValidShouldReturn204() {
      when(contractService.update(eq("1"), any(UpdateContractDTO.class))).thenReturn(Mono.empty());

      webTestClient
          .put()
          .uri("/api/contracts/1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(anUpdateContractDTO())
          .exchange()
          .expectStatus()
          .isNoContent();
    }

    @Test
    void testNegativeCostShouldReturn400() {
      UpdateContractDTO invalidUpdate =
          new UpdateContractDTO(LocalDate.now(), null, new Decimal128(-50));

      webTestClient
          .put()
          .uri("/api/contracts/1")
          .contentType(MediaType.APPLICATION_JSON)
          .bodyValue(invalidUpdate)
          .exchange()
          .expectStatus()
          .isBadRequest();
    }
  }

  @Nested
  class Delete {
    @Test
    void testShouldReturn204() {
      when(contractService.delete("1")).thenReturn(Mono.empty());

      webTestClient.delete().uri("/api/contracts/1").exchange().expectStatus().isNoContent();
    }
  }
}
