package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * CurrencyService handles requests related to currency
 */
@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyDataRepository) {
        this.repository = currencyDataRepository;
    }

    /**
     * This method returns Currency object at the specified currency ID
     *
     * @param currencyId identifier of Currency
     * @return Currency object with her cost
     */
    public Currency getCurrencyCost(String currencyId) {
        log.info("getting currency cost by currency id: {}", currencyId);
        return repository.getCurrencyById(currencyId);
    }

    /**
     * This method returns  a list of IDs for all available currencies
     */
    public List<String> getAllCurrenciesId() {
        log.info("getting currency ids");
        return repository.getAllCurrenciesIds();
    }

    /**
     * This method returns a collection of Currency objects for the specified date
     *
     * @param date
     */
    public Collection<Currency> getAllCurrency(LocalDate date) {
        return repository.getAllCurrency(date)
                .stream()
                .map(currency -> Currency.builder()
                        .id(currency.getId())
                        .value(currency.getValue())
                        .name(currency.getName())
                        .nominal(currency.getNominal())
                        .build())
                .collect(Collectors.toList());
    }

    /**
     * Thid method checks db is empty or not
     *
     * @return true id db is empty
     */
    public boolean dbIsEmpty() {
        return repository.isEmpty();
    }

    /**
     * This method returns a list of Currency at the specified period and by the specified identifiers
     *
     * @param startDate
     * @param endDate
     * @param idFirst
     * @param idSecond
     */
    public List<Currency> getCurrencyByPeriod(LocalDate startDate, LocalDate endDate, String idFirst, String idSecond) {
        return repository.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);
    }
}
