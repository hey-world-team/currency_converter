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
     * This method returns a Currency object at the specified currency ID
     *
     * @param currencyId identifier of the Currency
     * @return Currency object with its cost
     */
    public Currency getCurrencyCost(String currencyId) {
        log.info("getting currency cost by currency id: {}", currencyId);
        return repository.getCurrencyById(currencyId);
    }

    /**
     * This method returns  a list of IDs for all available currencies
     *
     * @return a list of Currency IDs
     */
    public List<String> getAllCurrenciesId() {
        log.info("getting currency ids");
        return repository.getAllCurrenciesIds();
    }

    /**
     * This method returns a collection of Currency objects for the specified date
     *
     * @param date the date for which to retrieve the currencies
     * @return a collection of Currency objects
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
     * This method checks if the DB is empty
     *
     * @return true if the DB is empty, false otherwise
     */
    public boolean dbIsEmpty() {
        return repository.isEmpty();
    }

    /**
     * This method returns a list of Currency objects within the specified period and with the specified identifiers
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @param idFirst   the identifier of the first currency
     * @param idSecond  the identifier of the second currency
     * @return a list of Currency objects
     */
    public List<Currency> getCurrencyByPeriod(LocalDate startDate, LocalDate endDate, String idFirst, String idSecond) {
        return repository.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);
    }
}