package ch.vaudoise.crm_api.service;

import ch.vaudoise.crm_api.model.dto.client.CreateClientDTO;
import ch.vaudoise.crm_api.model.dto.client.ResponseClientDTO;
import ch.vaudoise.crm_api.model.dto.client.UpdateClientDTO;
import ch.vaudoise.crm_api.model.entity.Client;
import ch.vaudoise.crm_api.model.exception.NotFoundException;
import ch.vaudoise.crm_api.repository.ClientRepository;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class ClientService {

  private final ClientRepository clientRepository;

  public ClientService(ClientRepository clientRepository) {
    this.clientRepository = clientRepository;
  }

  public Flux<ResponseClientDTO> findAll() {
    return clientRepository.findAll().map(Client::toDTO);
  }

  public Mono<ResponseClientDTO> find(String id) {
    return clientRepository
        .findById(new ObjectId(id))
        .map(Client::toDTO)
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)));
  }

  public Mono<Void> create(CreateClientDTO dto) {
    Client client =
        Client.builder()
            .type(dto.type())
            .name(dto.name())
            .phone(dto.phone())
            .email(dto.email())
            .birthday(dto.birthday())
            .companyIdentifier(dto.companyIdentifier())
            .build();

    return clientRepository.save(client).then();
  }

  public Mono<Void> update(String id, UpdateClientDTO dto) {
    return clientRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .flatMap(
            client -> {
              Client.ClientBuilder updateClient = client.toBuilder();
              if (dto.name() != null) updateClient.name(dto.name());
              if (dto.type() != null) updateClient.type(dto.type());
              if (dto.email() != null) updateClient.email(dto.email());
              if (dto.phone() != null) updateClient.phone(dto.phone());
              return clientRepository.save(updateClient.build());
            })
        .then();
  }

  public Mono<Void> delete(String id) {
    return clientRepository
        .findById(new ObjectId(id))
        .switchIfEmpty(Mono.error(new NotFoundException("Client not found: " + id)))
        .flatMap(clientRepository::delete);
  }
}
