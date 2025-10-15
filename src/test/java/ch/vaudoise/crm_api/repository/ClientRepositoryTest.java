package ch.vaudoise.crm_api.repository;

import static ch.vaudoise.crm_api.fixtures.ClientFixture.aClient;

import ch.vaudoise.crm_api.model.ClientType;
import ch.vaudoise.crm_api.model.entity.Client;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@SpringBootTest
@Testcontainers
class ClientRepositoryTest {

  @Container static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired private ClientRepository clientRepository;

  @BeforeEach
  void setup() {
    Client client1 =
        aClient().toBuilder()
            .id(new ObjectId("64c13ab08edf48a008793cac"))
            .name("Becca Smith")
            .build();
    Client client2 = aClient().toBuilder().id(new ObjectId()).name("Aaron Gordon").build();
    Client client3 = aClient().toBuilder().id(new ObjectId()).name("Mike John").build();

    clientRepository.save(client1).block();
    clientRepository.save(client2).block();
    clientRepository.save(client3).block();
  }

  @AfterEach
  void cleanup() {
    clientRepository.deleteAll().block();
  }

  @Test
  void shouldSaveAndFindClient() {
    Client mockClient = aClient().toBuilder().id(new ObjectId()).name("James Gauss").build();

    StepVerifier.create(
            clientRepository
                .save(mockClient)
                .flatMap(saved -> clientRepository.findById(saved.getId())))
        .assertNext(
            found -> {
              Assertions.assertNotNull(found.getId());
              Assertions.assertEquals("James Gauss", found.getName());
              Assertions.assertEquals(ClientType.PERSON, found.getType());
            })
        .verifyComplete();
  }

  @Test
  void shouldReturnAllClients() {
    StepVerifier.create(clientRepository.findAll()).expectNextCount(3).verifyComplete();
  }

  @Test
  void shouldDeleteClient() {
    ObjectId id = new ObjectId("64c13ab08edf48a008793cac");
    StepVerifier.create(
            clientRepository
                .findById(id)
                .flatMap(
                    saved -> clientRepository.delete(saved).then(clientRepository.findById(id))))
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyWhenNotFound() {
    StepVerifier.create(clientRepository.findById(new ObjectId("64c13ab08edf48a008793cad")))
        .verifyComplete();
  }
}
