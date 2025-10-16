package ch.vaudoise.crm_api.config;

import java.time.OffsetDateTime;
import java.util.Optional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;
import org.springframework.data.mongodb.core.mapping.event.ValidatingEntityCallback;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
@EnableReactiveMongoRepositories("ch.vaudoise.crm_api.repository")
@EnableReactiveMongoAuditing(dateTimeProviderRef = "auditingDateTimeProvider")
public class MongoConfig {
  @Bean(name = "auditingDateTimeProvider")
  public DateTimeProvider dateTimeProvider() {
    return () -> Optional.of(OffsetDateTime.now());
  }

  @Bean
  public ValidatingEntityCallback validatingEntityCallback(
      final LocalValidatorFactoryBean factory) {
    return new ValidatingEntityCallback(factory);
  }
}
