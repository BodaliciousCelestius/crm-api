package ch.vaudoise.crm_api.api.controller;

import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import ch.vaudoise.crm_api.service.ClientService;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SuppressFBWarnings(
    value = {"EI_EXPOSE_REP2"},
    justification = "Ignore warning on Spring Boot Dependency Injection EI_EXPOSE_REP2")
@RestController
@Validated
@RequestMapping(value = "/api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Clients", description = "Endpoints to manage clients")
public class ClientController {

  private final ClientService clientService;

  public ClientController(ClientService clientService) {
    this.clientService = clientService;
  }

  @Operation(
      summary = "Get client by ID",
      description = "Retrieves a client by their unique ID, including all relevant client details.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Client successfully retrieved"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}")
  public Mono<ResponseClientDTO> getClient(
      @Parameter(
              description = "ID of the client to retrieve",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id) {
    return clientService.findById(id);
  }

  @Operation(
      summary = "Get all active contracts for a client",
      description =
          "Retrieves all active contracts for a specific client, optionally filtered by start and end dates.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Contracts successfully retrieved"),
        @ApiResponse(responseCode = "400", description = "Invalid request"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}/contracts")
  public Flux<ResponseContractDTO> getAllContracts(
      @Parameter(
              description = "ID of the client to contract from",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id,
      @Parameter(
              description = "Filter contracts with last update date later than this",
              example = "2025-01-01")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate from,
      @Parameter(
              description = "Filter contracts with last update date earlier than this",
              example = "2025-12-31")
          @RequestParam(required = false)
          @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
          LocalDate to) {
    return clientService.getAllActiveContracts(id, from, to);
  }

  @Operation(
      summary = "Get total sum of client's active contracts",
      description = "Calculates and returns the total sum of all active contracts for a client.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Total sum successfully calculated"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @GetMapping("/{id}/contracts/total")
  public Mono<Decimal128> getAllContractsTotalSum(
      @Parameter(
              description = "ID of the client.",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id) {
    return clientService.getAllActiveContractsTotalSum(id);
  }

  @Operation(
      summary = "Create a new client",
      description =
          "Creates a new client in the system with personal details, contact information, and optional metadata. Returns the created client ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "201", description = "Client successfully created"),
        @ApiResponse(responseCode = "400", description = "Invalid client data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<String> createClient(
      @Parameter(description = "Client data to create", required = true) @RequestBody @Valid
          final CreateClientDTO client) {
    return clientService.create(client);
  }

  @Operation(
      summary = "Update an existing client",
      description =
          "Updates the details of an existing client, including contact information and optional metadata.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Client successfully updated"),
        @ApiResponse(responseCode = "400", description = "Invalid client data"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Void> updateClient(
      @Parameter(
              description = "ID of the client.",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id,
      @Parameter(description = "Updated client data", required = true) @RequestBody @Valid
          final UpdateClientDTO client) {
    return clientService.update(id, client);
  }

  @Operation(
      summary = "Delete a client",
      description = "Deletes an existing client by their unique ID.")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "204", description = "Client successfully deleted"),
        @ApiResponse(responseCode = "404", description = "Client not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
      })
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteClient(
      @Parameter(
              description = "ID of the client.",
              required = true,
              example = "4ecbe7f9e8c1c9092c000027")
          @PathVariable
          final String id) {
    return clientService.delete(id);
  }
}
