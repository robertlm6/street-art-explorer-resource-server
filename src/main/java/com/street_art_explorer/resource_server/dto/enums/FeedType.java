package com.street_art_explorer.resource_server.dto.enums;

public enum FeedType {
    newest, nearby, trending, top;

    public static FeedType from(String s) {
        if (s == null) return newest;
        return switch (s.toLowerCase()) {
            case "nearby" -> nearby;
            case "trending" -> trending;
            case "top" -> top;
            default -> newest;
        };
    }
}
