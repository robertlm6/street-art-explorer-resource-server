package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserAppDto {

    private Integer authServerUserId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
