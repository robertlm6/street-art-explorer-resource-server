package com.street_art_explorer.resource_server.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OwnerDto {
    private Integer id;
    private String username;
    private String avatarUrl;
}
