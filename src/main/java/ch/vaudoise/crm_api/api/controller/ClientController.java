package ch.vaudoise.crm_api.api.controller;

import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.service.ClientService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/clients", produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

  private final ClientService clientService;

  public ClientController(final ClientService clientService) {
    this.clientService = clientService;
  }

  @GetMapping
  public Flux<ResponseClientDTO> getAllClients() {
    return clientService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseClientDTO> getClient(@PathVariable final String id) {
    return clientService.find(id);
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Void> createClient(@RequestBody @Valid final CreateClientDTO client) {
    return clientService.create(client);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Void> updateClient(
      @PathVariable final String id, @RequestBody @Valid final UpdateClientDTO client) {
    return clientService.update(id, client);
  }

  @DeleteMapping("/{id}")
  @ApiResponse(responseCode = "204")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Void> deleteClient(@PathVariable final String id) {
    return clientService.delete(id);
  }
}
