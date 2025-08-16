package com.street_art_explorer.resource_server.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "marker_ratings")
@NoArgsConstructor
@AllArgsConstructor
@Data
@IdClass(MarkerRating.PK.class)
public class MarkerRating {

    @Id
    @Column(name = "marker_id")
    private Integer markerId;

    @Id
    @Column(name = "auth_server_user_id")
    private Integer authServerUserId;

    @Column(nullable = false)
    private Short score;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PK implements Serializable {
        private Integer markerId;
        private Integer authServerUserId;
    }
}
