package ch.vaudoise.crm_api.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

  @Bean
  public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
    return jacksonObjectMapperBuilder ->
        jacksonObjectMapperBuilder.featuresToDisable(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }
}
