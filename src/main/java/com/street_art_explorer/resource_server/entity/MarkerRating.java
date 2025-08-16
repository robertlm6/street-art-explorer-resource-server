package com.street_art_explorer.resource_server.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
