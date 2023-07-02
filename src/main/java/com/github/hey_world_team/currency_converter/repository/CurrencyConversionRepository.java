package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.CurrencyConversionHistory;

import java.time.LocalDate;
import java.util.List;

public interface CurrencyConversionRepository {
    String saveCurrencyConversion(CurrencyConversionHistory history);

    List<CurrencyConversionHistory> getCurrencyHistoryByInputCurrency(String inputCurrency);

    List<CurrencyConversionHistory> getAllCurrencyHistoryByDate(LocalDate date);

    boolean isEmptyHistory();

    void clearCurrencyConversionHistory();

}
