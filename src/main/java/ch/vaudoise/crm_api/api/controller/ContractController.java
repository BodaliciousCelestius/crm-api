package ch.vaudoise.crm_api.api.controller;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.service.ContractService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@Validated
@RequestMapping(value = "/api/contracts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContractController {

  private final ContractService contractService;

  @SuppressFBWarnings(justification = "Ignore warning on Spring Boot Dependency Injection EI_EXPOSE_REP2")
  public ContractController(final ContractService contractService) {
    this.contractService = contractService;
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<String> createContract(
      @RequestParam @NotBlank final String clientId,
      @RequestBody @Valid final CreateContractDTO contract) {
    return contractService.create(clientId, contract);
  }

  @PutMapping("/{id}")
  @ApiResponse(responseCode = "204")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> updateContract(
      @PathVariable final String id, @RequestBody @Valid final UpdateContractDTO contract) {
    return contractService.update(id, contract);
  }

  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteContract(@PathVariable final String id) {
    return contractService.delete(id);
  }
}
