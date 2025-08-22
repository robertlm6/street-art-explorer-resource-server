package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserAppDto {
    private Integer id;
    private Integer authServerUserId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private Date birthDate;
    private String bio;
    private String avatarUrl;
    private String avatarPublicId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
