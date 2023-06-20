package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;

import java.util.List;

public interface CurrencyRepository {
    String saveCurrency(Currency currency);

    Currency getCurrencyById(String id);

    Currency updateCurrency(Currency currency);

    List<Currency> getAllCurrency();

    List<String> getAllCurrenciesIds();
}
