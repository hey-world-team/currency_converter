package com.github.hey_world_team.currency_converter.service.currency;

public enum CurrencyType {
    RUB,
    EUR,
    USD;
    public static boolean contains(String value) {
        for (CurrencyType exchange : CurrencyType.values()) {
            if (exchange.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
