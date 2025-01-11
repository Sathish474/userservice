package com.sathish.userservice.security.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sathish.userservice.security.CustomSpringUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.addMixIn(java.sql.Timestamp.class, SqlTimestampMixin.class);
        mapper.addMixIn(CustomSpringUserDetails.class, CustomSpringUserDetailsMixin.class);
        return mapper;
    }
}
