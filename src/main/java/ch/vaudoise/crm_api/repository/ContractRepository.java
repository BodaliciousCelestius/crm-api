package ch.vaudoise.crm_api.repository;

import ch.vaudoise.crm_api.model.entity.Contract;
import java.time.Instant;
import java.time.LocalDate;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Update;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ContractRepository extends ReactiveMongoRepository<Contract, ObjectId> {

  Flux<Contract> findByClientIdAndEndDateGreaterThanEqual(ObjectId clientId, LocalDate date);

  Flux<Contract> findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtGreaterThanEqual(
      ObjectId clientId, LocalDate endDate, Instant from);

  Flux<Contract> findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtLessThanEqual(
      ObjectId clientId, LocalDate endDate, Instant to);

  Flux<Contract> findByClientIdAndEndDateGreaterThanEqualAndUpdatedAtBetween(
      ObjectId clientId, LocalDate endDate, Instant from, Instant to);

  @Query("{'clientId': ?0}")
  @Update("{ '$set' : { 'endDate' : ?1 } }")
  Mono<Void> setEndDateByClientId(ObjectId clientId, LocalDate date);

  @Query("{ 'clientId' : ?0 }")
  @Update("{ '$set' : { 'clientId' : null } }")
  Mono<Void> unsetClientIdByClientId(ObjectId clientId);

  @Aggregation(
      pipeline = {
        "{ $match: { clientId: ?0, endDate: { $gte: ?1 } } }",
        "{ $group: { _id: null, totalCost: { $sum: '$cost' } } }",
        "{ $set: { totalCost: { $convert: { input: '$totalCost', to: 'decimal' } } } }",
      })
  Mono<Decimal128> sumActiveContractTotalCostByClientId(ObjectId clientId, LocalDate date);
}
