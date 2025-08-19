package com.street_art_explorer.resource_server.service.implementation;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Override
    public Optional<Integer> getOptionalAuthId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof Jwt jwt)) {
            return Optional.empty();
        }
        Number n = jwt.getClaim("id");
        return Optional.ofNullable(n).map(Number::intValue);
    }
}
