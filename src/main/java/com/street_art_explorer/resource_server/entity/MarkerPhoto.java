package com.street_art_explorer.resource_server.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "marker_photos")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class MarkerPhoto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "marker_id")
    private Marker marker;

    @Column(nullable = false, length = 255)
    private String publicId;

    @Column(nullable = false, columnDefinition = "text")
    private String url;

    @Column(nullable = false, columnDefinition = "text")
    private String secureUrl;

    private String format;
    private Integer width;
    private Integer height;
    private Integer bytes;
    private String assetId;
    private String thumbnailUrl;
    private Short position;

    private LocalDateTime createdAt;

    @PrePersist
    void prePersist() {
        createdAt = LocalDateTime.now();
    }
}
