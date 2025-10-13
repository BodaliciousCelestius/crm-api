package ch.vaudoise.crm_api.repository;

import ch.vaudoise.crm_api.model.entity.Contract;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContractRepository extends ReactiveMongoRepository<Contract, ObjectId> {}
