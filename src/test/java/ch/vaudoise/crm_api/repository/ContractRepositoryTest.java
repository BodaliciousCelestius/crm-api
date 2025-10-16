package ch.vaudoise.crm_api.repository;

import static ch.vaudoise.crm_api.fixtures.ClientFixture.aClient;

import ch.vaudoise.crm_api.model.entity.Client;
import ch.vaudoise.crm_api.model.entity.Contract;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

@DataMongoTest
@Testcontainers
class ContractRepositoryTest {

  @Container final static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest");

  @DynamicPropertySource
  static void setProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
  }

  @Autowired private ContractRepository contractRepository;

  private Client client;

  @BeforeEach
  void setup() {
    client = aClient();

    Contract contract1 =
        Contract.builder()
            .id(new ObjectId("650f2e4c1b6d4c7f8a9b1234"))
            .clientId(client.getId())
            .startDate(LocalDate.now().minusDays(10))
            .endDate(LocalDate.now().plusDays(10))
            .cost(new Decimal128(100))
            .updatedAt(Instant.now())
            .build();

    Contract contract2 =
        Contract.builder()
            .id(new ObjectId("507f1f77bcf86cd799439025"))
            .clientId(client.getId())
            .startDate(LocalDate.now().minusDays(20))
            .endDate(LocalDate.now().plusMonths(1))
            .cost(new Decimal128(42))
            .updatedAt(Instant.now().minusSeconds(1000000000))
            .build();

    Contract contract3 =
        Contract.builder()
            .id(new ObjectId("5f8d0d55b54764421b7156c3"))
            .clientId(client.getId())
            .startDate(LocalDate.now().minusDays(20))
            .endDate(LocalDate.now().plusDays(1))
            .cost(Decimal128.parse("200.30"))
            .updatedAt(Instant.now())
            .build();

    Contract contract4 =
        Contract.builder()
            .id(new ObjectId("64f3a6d29e1b4a001234abcd"))
            .clientId(client.getId())
            .startDate(LocalDate.now().minusDays(20))
            .endDate(LocalDate.now().minusDays(1))
            .cost(new Decimal128(12))
            .updatedAt(Instant.now())
            .build();

    Contract contract5 =
        Contract.builder()
            .id(new ObjectId("507f1f77bcf86cd799439011"))
            .clientId(client.getId())
            .startDate(LocalDate.now().minusDays(45))
            .endDate(LocalDate.now().minusDays(3))
            .cost(new Decimal128(2))
            .updatedAt(Instant.now())
            .build();

    Contract contract6 =
        Contract.builder()
            .id(new ObjectId("507f1f77bc586cd794439025"))
            .clientId(new ObjectId("507f1f57bff86fd799435014"))
            .startDate(LocalDate.now().minusMonths(8))
            .endDate(LocalDate.now().plusDays(5))
            .cost(new Decimal128(45))
            .updatedAt(Instant.now())
            .build();

    List<Contract> contracts =
        List.of(contract1, contract2, contract3, contract4, contract5, contract6);

    contractRepository.saveAll(contracts).blockLast();
  }

  @AfterEach
  void cleanup() {
    contractRepository.deleteAll().block();
  }

  @Test
  void shouldFindActiveContracts() {
    StepVerifier.create(
            contractRepository.findByClientIdAndEndDateGreaterThanEqual(
                client.getId(), LocalDate.now()))
        .expectNextCount(3)
        .verifyComplete();
  }

  @Test
  void shouldSumActiveContractTotalCost() {
    StepVerifier.create(
            contractRepository.sumActiveContractTotalCostByClientId(
                client.getId(), LocalDate.now()))
        .expectNextMatches(
            sum -> sum != null && sum.bigDecimalValue().compareTo(BigDecimal.valueOf(342.3)) == 0)
        .verifyComplete();
  }

  @Test
  void shouldUpdateEndDate() {
    LocalDate now = LocalDate.now();
    contractRepository.setEndDateByClientId(client.getId(), now).block();

    StepVerifier.create(
            contractRepository
                .findAll()
                .filter(contract -> contract.getClientId() == client.getId())
                .collectList())
        .expectNextMatches(
            contractsArg ->
                contractsArg.stream().allMatch(contract -> contract.getEndDate().equals(now)))
        .verifyComplete();
  }

  @Test
  void shouldUnsetClientIdsUponDeletion() {
    contractRepository.unsetClientIdByClientId(client.getId()).block();

    StepVerifier.create(
            contractRepository
                .findAll()
                .filter(contract -> contract.getClientId() == client.getId())
                .collectList())
        .expectNextMatches(
            contractsArg -> contractsArg.stream().allMatch(contract -> client.getId() == null))
        .verifyComplete();
  }

  @Test
  void shouldReturnEmptyForNoActiveContracts() {
    StepVerifier.create(
            contractRepository.sumActiveContractTotalCostByClientId(
                client.getId(), LocalDate.now().plusYears(100)))
        .verifyComplete();
  }

  @Test
  void shouldFindContractsByUpdatedAtBetween() {
    Instant from = LocalDate.now().minusDays(15).atStartOfDay().toInstant(ZoneOffset.UTC);
    Instant to = LocalDate.now().plusDays(5).atStartOfDay().toInstant(ZoneOffset.UTC);

    StepVerifier.create(
            contractRepository.findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtBetween(
                client.getId(), LocalDate.now(), from, to))
        .expectNextCount(2)
        .verifyComplete();
  }
}
