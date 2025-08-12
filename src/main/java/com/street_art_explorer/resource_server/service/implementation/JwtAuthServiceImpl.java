package com.street_art_explorer.resource_server.service.implementation;

import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import com.street_art_explorer.resource_server.service.JwtAuthService;

import lombok.NoArgsConstructor;

@Service
@NoArgsConstructor
public class JwtAuthServiceImpl implements JwtAuthService {

    @Override
    public Integer requireAuthId(Jwt jwt) {
        if (jwt == null) {
            throw new IllegalStateException("No JWT present");
        }
        Number authId = jwt.getClaim("id");
        if (authId == null) {
            throw new IllegalStateException("No 'id' claim present");
        }
        return authId.intValue();
    }
}
