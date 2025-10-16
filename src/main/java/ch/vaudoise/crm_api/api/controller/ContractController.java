package ch.vaudoise.crm_api.api.controller;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.service.ContractService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Contracts", description = "Endpoints to manage contracts")
public class ContractController {

  private final ContractService contractService;

  @SuppressFBWarnings(
      justification = "Ignore warning on Spring Boot Dependency Injection EI_EXPOSE_REP2")
  public ContractController(final ContractService contractService) {
    this.contractService = contractService;
  }

  @Operation(
      summary = "Create a new contract",
      description =
          "Creates a new contract in the system with start date, end date and cost information. Returns the created contract id.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Client successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid client data"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<String> createContract(
      @Parameter(
              description = "ID of the client.",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @RequestParam
          @NotBlank
          final String clientId,
      @Parameter(description = "New contract data", required = true) @RequestBody @Valid
          final CreateContractDTO contract) {
    return contractService.create(clientId, contract);
  }

  @Operation(
      summary = "Update an existing contract",
      description =
          "Updates the details of an existing contract, including start date, end date, and cost information.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Contract successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid contract data"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> updateContract(
      @Parameter(
              description = "ID of the contract.",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id,
      @Parameter(description = "Update contract data", required = true) @RequestBody @Valid
          final UpdateContractDTO contract) {
    return contractService.update(id, contract);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Delete a contract", description = "Deletes an existing contract by its ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Contract successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Contract not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  public Mono<Void> deleteContract(
      @Parameter(
              description = "ID of the contract.",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id) {
    return contractService.delete(id);
  }
}
