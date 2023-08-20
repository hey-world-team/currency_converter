package com.github.hey_world_team.currency_converter.service.exchange;

public enum ChoosedCoin {
    USDT,
    ETH,
    BTC,
    ALL;
    public static boolean contains(String value) {
        for (ChoosedCoin coin : ChoosedCoin.values()) {
            if (coin.name().equalsIgnoreCase(value)) {
                return true;
            }
        }
        return false;
    }
}
