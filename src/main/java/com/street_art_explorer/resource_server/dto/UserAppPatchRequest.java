package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.util.Date;

public record UserAppPatchRequest(
        @Size(max = 100)
        String firstName,

        @Size(max = 100)
        String lastName,

        @Past
        Date birthDate,

        @Size(max = 1000)
        String bio,

        @Size(max = 2048)
        @URL
        String avatarUrl,

        @Size(max = 255)
        String avatarPublicId) {
}
