package com.street_art_explorer.resource_server.dto;

public enum Period {
    month, year, all;

    public static Period from(String raw) {
        if (raw == null) return all;
        try {
            return Period.valueOf(raw.toLowerCase());
        } catch (IllegalArgumentException ex) {
            return all;
        }
    }
}
