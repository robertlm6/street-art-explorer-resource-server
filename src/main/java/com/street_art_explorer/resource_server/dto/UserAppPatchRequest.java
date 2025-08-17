package com.street_art_explorer.resource_server.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAppPatchRequest {

    @Size(max = 100)
    private String firstName;

    @Size(max = 100)
    private String lastName;

    @Past
    private Date birthDate;

    @Size(max = 1000)
    private String bio;

    @Size(max = 2048)
    private String avatarUrl;

    @Size(max = 255)
    private String avatarPublicId;
}
