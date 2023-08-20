package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.CurrencyDep;

import java.time.LocalDate;
import java.util.List;

/**
 * CurrencyRepository provides methods for working with the Currency data store
 */
public interface CurrencyRepository {

    String saveCurrency(CurrencyDep currencyDep);

    CurrencyDep getCurrencyById(String id);

    CurrencyDep updateCurrency(CurrencyDep currencyDep);

    List<CurrencyDep> getAllCurrency(LocalDate date);

    List<String> getAllCurrenciesIds();

    boolean isEmpty();

    int saveCurrencies(List<CurrencyDep> currencies);

    int updateCurrencies(List<CurrencyDep> currencies);

    List<CurrencyDep> getCurrencyByPeriod(LocalDate startDate, LocalDate endDate, String idFirst, String idSecond);
}