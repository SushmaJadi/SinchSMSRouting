package com.sinch.sms.routing.configuration;

import com.sinch.sms.routing.util.PhoneNumberUtility;
import org.springframework.boot.jackson.autoconfigure.JsonMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tools.jackson.databind.DeserializationFeature;

@Configuration
public class JsonBuilderConfiguration {
    @Bean
    public JsonMapperBuilderCustomizer customizer() {
        return builder -> builder
                .disable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES);
    }

}
