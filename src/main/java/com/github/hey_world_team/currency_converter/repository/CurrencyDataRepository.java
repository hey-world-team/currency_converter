package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.dto.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.*;

@Repository
public class CurrencyDataRepository {
    private static final Logger log = LoggerFactory.getLogger(CurrencyDataRepository.class);
    private final Map<String, Currency> repository = new HashMap<>();

    public void save(Currency currency) {
        log.info("save currency id: {}, name: {}, cost: {}", currency.getId(), currency.getName(), currency.getValue());
        repository.put(currency.getId(), currency);
    }

    public Currency getCurrencyValueById(String currencyId) {
        if (repository.containsKey(currencyId)) {
            log.info("currency with id {} found", currencyId);
            return repository.get(currencyId);
        } else {
            log.info("currency with id {} NOT found", currencyId);
            return null;

        }
    }

    public Collection<Currency> getAllCurrencies() {
        return repository.values();
    }

    public List<String> getAllCurrenciesId() {
        List<String> ids = new ArrayList<>();
        repository.values().forEach(x -> ids.add(x.getId()));
        return ids;
    }
}
