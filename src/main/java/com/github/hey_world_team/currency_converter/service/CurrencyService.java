package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyDataRepository) {
        this.repository = currencyDataRepository;
    }

    public Currency getCurrencyCost(String currencyId) {
        log.info("getting currency cost by currency id: {}", currencyId);
        Currency currency = repository.getCurrencyById(currencyId);
        return currency;
    }

    public List<String> getAllCurrenciesId() {
        log.info("getting currency ids");
        return repository.getAllCurrenciesIds();
    }

    public Collection<Currency> getAllCurrency() {
        return repository.getAllCurrency()
                .stream()
                .map(currency -> Currency.builder()
                        .id(currency.getId())
                        .value(currency.getValue())
                        .name(currency.getName())
                        .nominal(currency.getNominal())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean dbIsEmpty() {
        return repository.isEmpty();
    }
}
