package ch.vaudoise.crm_api.api;

import ch.vaudoise.crm_api.model.dto.contract.CreateContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.ResponseContractDTO;
import ch.vaudoise.crm_api.model.dto.contract.UpdateContractDTO;
import ch.vaudoise.crm_api.service.ContractService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping(value = "/api/contracts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContractController {

  private final ContractService contractService;

  public ContractController(final ContractService contractService) {
    this.contractService = contractService;
  }

  @GetMapping
  public Flux<ResponseContractDTO> getAllContracts() {
    return contractService.findAll();
  }

  @GetMapping("/{id}")
  public Mono<ResponseContractDTO> getContract(@PathVariable final String id) {
    return contractService.find(id);
  }

  @PostMapping
  @ApiResponse(responseCode = "201")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Void> createContract(
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
