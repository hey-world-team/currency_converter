package com.github.hey_world_team.currency_converter.repository;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class CurrencyDataRepository {
    private Map<String, Double> repository = new HashMap<>();

    public void save(String name, Double value) {
        repository.put(name, value);
    }

    public Double getCurrencyValueByName(String name) {
        return repository.get(name);
    }

    public Map<String, Double> getAllCurrencies() {
        return repository;
    }

    public void updateCurrencyValueByName(String name, Double value) {
        repository.replace(name, value);
    }

    public void updateCurrencyValues(Map <String, Double> newMap) {
        repository.putAll(newMap);
    }
}
