package com.github.hey_world_team.currency_converter.service;


import com.github.hey_world_team.currency_converter.model.CurrencyConversionHistory;
import com.github.hey_world_team.currency_converter.repository.CurrencyConversionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class CurrencyConversionService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyConversionService.class);
    private final CurrencyConversionRepository historyRepository;

    @Autowired
    public CurrencyConversionService(CurrencyConversionRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    /**
     * This method adds conversion history to the database
     *
     * @param conversionHistory Conversion history object to be added
     * @return The added conversion history
     */
    public String addConversionHistory(CurrencyConversionHistory conversionHistory) {
        log.info("Adding conversion history: {}", conversionHistory);
        return historyRepository.saveCurrencyConversion(conversionHistory);
    }


    public List<CurrencyConversionHistory> getCurrencyHistory() {
        return historyRepository.getAllCurrencyHistory();
    }


    public String saveCurrencyConversion(CurrencyConversionHistory history) {
        return historyRepository.saveCurrencyConversion(history);
    }

    public List<CurrencyConversionHistory> getCurrencyHistoryById(String inputCurrency) {
        // Реализуйте получение истории конвертации по идентификатору
        return historyRepository.getCurrencyHistoryByInputCurrency(inputCurrency);
    }

    public List<CurrencyConversionHistory> getAllCurrencyHistoryByDate(LocalDate date) {
        return historyRepository.getAllCurrencyHistoryByDate(date);
    }

    public boolean isEmpty() {
        return historyRepository.isEmptyHistory();
    }

    public void clearCurrencyConversionHistory() {
        historyRepository.clearCurrencyConversionHistory();
    }
}








