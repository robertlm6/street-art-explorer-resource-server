package com.street_art_explorer.resource_server.repository;

import com.street_art_explorer.resource_server.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp, Integer> {
    Optional<UserApp> findByAuthServerUserId(Integer authServerUserId);
}
