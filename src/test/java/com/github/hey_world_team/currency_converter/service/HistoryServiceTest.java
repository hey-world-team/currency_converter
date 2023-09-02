package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.model.History;


import com.github.hey_world_team.currency_converter.repository.HistoryRepository;
import com.github.hey_world_team.currency_converter.service.history.HistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class HistoryServiceTest {

    @Mock
    private HistoryRepository historyRepository;

    @InjectMocks
    private HistoryService historyService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void saveHistory_WhenSavedSuccessfully() {
        // Arrange
        History history = new History(1, LocalDate.now(), "USD", new BigDecimal(100), "EUR", new BigDecimal(80));
        when(historyRepository.saveHistory(history)).thenReturn(1);

        // Act
        int result = historyService.saveHistory(history);

        // Assert
        assertEquals(1, result);
        verify(historyRepository, times(1)).saveHistory(history);
    }


    @Test
    public void getCurrencyHistory() {
        // Arrange
        List<History> expectedHistory = new ArrayList<>();
        expectedHistory.add(new History(1, LocalDate.now(), "USD", new BigDecimal(100), "EUR", new BigDecimal(80)));
        expectedHistory.add(new History(2, LocalDate.now(), "EUR", new BigDecimal(80), "USD", new BigDecimal(100)));
        when(historyRepository.getAllCurrencyHistory()).thenReturn(expectedHistory);

        // Act
        List<History> result = historyService.getCurrencyHistory();

        // Assert
        assertEquals(expectedHistory, result);
        verify(historyRepository, times(1)).getAllCurrencyHistory();
    }

    @Test
    public void getCurrencyHistoryByInputCurrency_WhenInputCurrencyExists() {
        // Arrange
        String inputCurrency = "USD";
        List<History> expectedHistory = new ArrayList<>();
        expectedHistory.add(new History(1, LocalDate.now(), "USD", new BigDecimal(100), "EUR", new BigDecimal(80)));
        expectedHistory.add(new History(2, LocalDate.now(), "EUR", new BigDecimal(80), "USD", new BigDecimal(100)));
        when(historyRepository.getCurrencyHistoryByInputCurrency(inputCurrency)).thenReturn(expectedHistory);

        // Act
        List<History> result = historyService.getCurrencyHistoryByInputCurrency(inputCurrency);

        // Assert
        assertEquals(expectedHistory, result);
        verify(historyRepository, times(1)).getCurrencyHistoryByInputCurrency(inputCurrency);
    }

    @Test
    public void getAllCurrencyHistoryByDate_WhenDateExists() {
        // Arrange
        LocalDate date = LocalDate.now();
        List<History> expectedHistory = new ArrayList<>();
        expectedHistory.add(new History(1, LocalDate.now(), "USD", new BigDecimal(100), "EUR", new BigDecimal(80)));
        expectedHistory.add(new History(2, LocalDate.now(), "EUR", new BigDecimal(80), "USD", new BigDecimal(100)));
        when(historyRepository.getAllCurrencyHistoryByDate(date)).thenReturn(expectedHistory);

        // Act
        List<History> result = historyService.getAllCurrencyHistoryByDate(date);

        // Assert
        assertEquals(expectedHistory, result);
        verify(historyRepository, times(1)).getAllCurrencyHistoryByDate(date);
    }

    @Test
    public void dbHistoryIsEmpty() {
        // Arrange
        when(historyRepository.isEmptyHistory()).thenReturn(true);

        // Act
        boolean result = historyService.dbHistoryIsEmpty();

        // Assert
        assertTrue(result);
        verify(historyRepository, times(1)).isEmptyHistory();
    }

    @Test
    public void clearCurrencyConversionHistory_ShouldCallHistoryRepositoryClearHistory() {
        // Act
        historyService.clearCurrencyConversionHistory();

        // Assert
        verify(historyRepository, times(1)).clearHistory();
    }

    @Test
    public void getCurrencyHistory_WithEmptyHistory() {
        // Arrange
        when(historyRepository.getAllCurrencyHistory()).thenReturn(new ArrayList<>());

        // Act
        List<History> history = historyService.getCurrencyHistory();

        // Assert
        assertTrue(history.isEmpty());
    }

    @Test
    public void getCurrencyHistoryByInputCurrency_WithEmptyHistory() {
        // Arrange
        String inputCurrency = "USD";
        when(historyRepository.getCurrencyHistoryByInputCurrency(inputCurrency)).thenReturn(new ArrayList<>());

        // Act
        List<History> history = historyService.getCurrencyHistoryByInputCurrency(inputCurrency);

        // Assert
        assertTrue(history.isEmpty());
    }

    @Test
    public void getAllCurrencyHistoryByDate_WithEmptyHistory() {
        // Arrange
        LocalDate date = LocalDate.now();
        when(historyRepository.getAllCurrencyHistoryByDate(date)).thenReturn(new ArrayList<>());

        // Act
        List<History> history = historyService.getAllCurrencyHistoryByDate(date);

        // Assert
        assertTrue(history.isEmpty());
    }
}
