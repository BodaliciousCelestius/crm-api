package ch.vaudoise.crm_api.repository;

import ch.vaudoise.crm_api.model.entity.Client;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends ReactiveMongoRepository<Client, ObjectId> {}
