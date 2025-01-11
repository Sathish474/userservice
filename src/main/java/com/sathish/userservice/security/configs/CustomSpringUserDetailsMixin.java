package com.sathish.userservice.security.configs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sathish.userservice.models.User;

public abstract class CustomSpringUserDetailsMixin {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("user")
    private User user;
}