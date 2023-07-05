package com.github.hey_world_team.currency_converter.repository;


import com.github.hey_world_team.currency_converter.model.History;

import java.time.LocalDate;
import java.util.List;

/**
 * HistoryRepository provides methods for working with the History data store
 */
public interface HistoryRepository {

    int saveHistory(History history);

    List<History> getCurrencyHistoryByInputCurrency(String inputCurrency);

    List<History> getAllCurrencyHistoryByDate(LocalDate date);

    boolean isEmptyHistory();

    List<History> getAllCurrencyHistory();

    void clearHistory();

}
