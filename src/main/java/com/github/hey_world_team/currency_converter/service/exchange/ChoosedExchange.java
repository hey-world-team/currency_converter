package com.github.hey_world_team.currency_converter.service.exchange;

public enum ChoosedExchange {
    ALL;

    public static boolean contains(String value) {
        for (ChoosedExchange exchange : ChoosedExchange.values()) {
            if (exchange.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}