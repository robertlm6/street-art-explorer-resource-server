package com.street_art_explorer.resource_server.service;

import org.springframework.security.oauth2.jwt.Jwt;

public interface JwtAuthService {
    Integer requireAuthId(Jwt jwt);
}
