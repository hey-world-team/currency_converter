package com.github.hey_world_team.currency_converter.service;


import com.github.hey_world_team.currency_converter.model.History;
import com.github.hey_world_team.currency_converter.repository.HistoryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

/**
 * HistoryService handles requests related to conversion history
 */
@Service
public class HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryService.class);
    private final HistoryRepository historyRepository;

    @Autowired
    public HistoryService(HistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * This method saves the currency conversion history to the database
     *
     * @param history the currency conversion history object to be added
     * @return the added History object
     */
    public int saveHistory(History history) {
        log.info("Adding conversion history: {}", history);
        return historyRepository.saveHistory(history);
    }

    /**
     * This method retrieves the list of all currency conversion history
     *
     * @return the list of History objects
     */
    public List<History> getCurrencyHistory() {
        return historyRepository.getAllCurrencyHistory();
    }

    /**
     * This method returns the currency conversion history object by the input currency
     *
     * @param inputCurrency the input currency for which to retrieve the history
     * @return the list of History objects for the specified input currency
     */
    public List<History> getCurrencyHistoryByInputCurrency(String inputCurrency) {
        return historyRepository.getCurrencyHistoryByInputCurrency(inputCurrency);
    }

    /**
     * This method returns all currency conversion history for a specific date
     *
     * @param date the date for which to retrieve the history
     * @return the list of History objects for the specified date
     */
    public List<History> getAllCurrencyHistoryByDate(LocalDate date) {
        return historyRepository.getAllCurrencyHistoryByDate(date);
    }

    /**
     * This method checks if history of conversion is empty
     *
     * @return true if the history is empty, false otherwise
     */
    public boolean dbHistoryIsEmpty() {
        return historyRepository.isEmptyHistory();
    }

    /**
     * This method clears all history of conversion
     */
    public void clearCurrencyConversionHistory() {
        historyRepository.clearHistory();
    }
}