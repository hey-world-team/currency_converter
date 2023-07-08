package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyRepository {
    String saveCurrency(Currency currency);

    Currency getCurrencyById(String id);

    List<Currency> getCurrenciesByIds(List<String> ids);

    Currency updateCurrency(Currency currency);

    List<Currency> getAllCurrency(LocalDate date);

    List<String> getAllCurrenciesIds();

    boolean isEmpty();

    int saveCurrencies(List<Currency> currencies);

    int updateCurrencies(List<Currency> currencies);

    List<Currency> getCurrencyByPeriod(LocalDate startDate, LocalDate endDate, String idFirst, String idSecond);
}
