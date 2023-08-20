package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.CurrencyDep;
import com.github.hey_world_team.currency_converter.model.History;
import com.github.hey_world_team.currency_converter.model.Value;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ContextConfiguration;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ContextConfiguration(classes = {CurrencyDepRepositoryImplTest.TestConfiguration.class})
public class CurrencyDepRepositoryImplTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    private CurrencyRepositoryImpl currencyRepository;

    @Configuration
    public static class TestConfiguration {
        @Bean
        public DataSource dataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("org.postgresql.Driver");
            dataSource.setUrl("jdbc:postgresql://localhost:5432/test");
            //TODO change user to test-user
            dataSource.setUsername("postgres");
            dataSource.setPassword("pass");
            return dataSource;
        }

        @Bean
        public JdbcTemplate jdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }
    }

    @PostConstruct
    public void setup() {
        currencyRepository = new CurrencyRepositoryImpl(jdbcTemplate);
        String dropTableQuery = "DROP TABLE IF EXISTS value CASCADE;\n" +
                "DROP TABLE IF EXISTS currency CASCADE;\n" +
                "DROP TABLE IF EXISTS history CASCADE;";
        jdbcTemplate.execute(dropTableQuery);

        String createTableQuery = "CREATE TABLE currency (id VARCHAR(3) PRIMARY KEY, name VARCHAR(250) NOT NULL, nominal INT);\n" +
                "CREATE TABLE value (uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(), currency_id VARCHAR(3), value NUMERIC NOT NULL, date DATE NOT NULL, FOREIGN KEY (currency_id) REFERENCES currency(id));\n" +
                "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";\n" +
                "CREATE TABLE IF NOT EXISTS history (id SERIAL PRIMARY KEY, conversion_date DATE NOT NULL, input_currency VARCHAR(3) NOT NULL, input_amount DECIMAL(18, 2) NOT NULL, output_currency VARCHAR(3) NOT NULL, output_amount DECIMAL(18, 2) NOT NULL);";
        jdbcTemplate.execute(createTableQuery);
    }

    @Test
    public void saveCurrency_ShouldReturnId_WhenSavedSuccessfully() {
        // Arrange
        CurrencyDep currencyDep = new CurrencyDep();
        currencyDep.setId("USD");
        currencyDep.setName("US Dollar");
        currencyDep.setNominal(1);
        currencyDep.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act
        String id = currencyRepository.saveCurrency(currencyDep);

        // Assert
        assertNotNull(id);
        assertEquals("USD", id);
    }

    @Test
    public void updateCurrency_ShouldReturnUpdatedCurrency_WhenUpdateSuccessful() {
        // Arrange
        CurrencyDep currencyDep = new CurrencyDep();
        currencyDep.setId("USD");
        currencyDep.setName("US");
        currencyDep.setNominal(1);
        currencyDep.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        CurrencyDep currencyDepForUpdate = new CurrencyDep();
        currencyDepForUpdate.setId("USD");
        currencyDepForUpdate.setName("US Dollar");
        currencyDepForUpdate.setNominal(10);
        currencyDepForUpdate.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act
        String id = currencyRepository.saveCurrency(currencyDep);
        CurrencyDep updatedCurrencyDep = currencyRepository.updateCurrency(currencyDepForUpdate);

        // Assert
        assertNotNull(id);
        assertEquals("USD", id);
        assertNotNull(updatedCurrencyDep);
        assertEquals(currencyDepForUpdate.getId(), updatedCurrencyDep.getId());
        assertEquals(currencyDepForUpdate.getNominal(), updatedCurrencyDep.getNominal());
        assertEquals(currencyDepForUpdate.getValue(), updatedCurrencyDep.getValue());
        assertEquals(currencyDepForUpdate.getValue().getDate(), updatedCurrencyDep.getValue().getDate());
    }

    @Test
    public void saveCurrencies_ShouldReturnRowCount_WhenSavedSuccessfully() {
        // Arrange
        CurrencyDep currencyDep1 = new CurrencyDep();
        currencyDep1.setId("USD");
        currencyDep1.setName("US Dollar");
        currencyDep1.setNominal(1);
        currencyDep1.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        CurrencyDep currencyDep2 = new CurrencyDep();
        currencyDep2.setId("EUR");
        currencyDep2.setName("Euro");
        currencyDep2.setNominal(1);
        currencyDep2.setValue(new Value(BigDecimal.valueOf(0.9), LocalDate.now()));

        List<CurrencyDep> currencies = Arrays.asList(currencyDep1, currencyDep2);

        // Act
        int rowCount = currencyRepository.saveCurrencies(currencies);

        // Assert
        assertEquals(2, rowCount);
    }

    @Test
    public void saveCurrency_ShouldThrowException_WhenSaveFails() {
        // Arrange
        CurrencyDep currencyDep = new CurrencyDep();
        currencyDep.setId("USD");
        currencyDep.setName("US Dollar");
        currencyDep.setNominal(1);
        currencyDep.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Create a mock JdbcTemplate that always returns 0 rows affected
        JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);
        when(mockJdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);

        CurrencyRepositoryImpl currencyRepository = new CurrencyRepositoryImpl(mockJdbcTemplate);
        // Act and Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> currencyRepository.saveCurrency(currencyDep)
        );
        assertEquals("Failed to save currency with id: USD", exception.getMessage());
    }

    @Test
    public void getCurrencyById_ShouldReturnCurrency_WhenIdExists() {
        // Arrange
        String id = "USD";

        CurrencyDep currencyDep = new CurrencyDep();
        currencyDep.setId(id);
        currencyDep.setName("US Dollar");
        currencyDep.setNominal(1);
        currencyDep.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act
        String savedId = currencyRepository.saveCurrency(currencyDep);
        CurrencyDep currencyDepFromDB = currencyRepository.getCurrencyById(id);

        // Assert
        assertNotNull(savedId);
        assertEquals(id, savedId);
        assertNotNull(currencyDepFromDB);
        assertEquals(id, currencyDepFromDB.getId());
    }

    @Test
    public void getCurrencyById_ShouldReturnNull_WhenIdDoesNotExist() {
        // Arrange
        String id = "INVALID_ID";

        // Act
        CurrencyDep currencyDep = currencyRepository.getCurrencyById(id);

        // Assert
        assertNull(currencyDep);
    }

    @Test
    public void updateCurrency_ShouldThrowException_WhenUpdateFails() {
        // Arrange
        CurrencyDep currencyDep = new CurrencyDep();
        currencyDep.setId("INVALID_ID");
        currencyDep.setName("Invalid Currency");
        currencyDep.setNominal(1);
        currencyDep.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act and Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> currencyRepository.updateCurrency(currencyDep)
        );
        assertEquals("Failed to update currency with id: " + currencyDep.getId(), exception.getMessage());
    }

    @Test
    public void getAllCurrency_ShouldReturnListOfCurrencies_WhenDateExists() {
        // Arrange
        LocalDate date = LocalDate.now();

        CurrencyDep currencyDep1 = new CurrencyDep();
        currencyDep1.setId("USD");
        currencyDep1.setName("US Dollar");
        currencyDep1.setNominal(1);
        currencyDep1.setValue(new Value(BigDecimal.valueOf(1.0), date));

        CurrencyDep currencyDep2 = new CurrencyDep();
        currencyDep2.setId("EUR");
        currencyDep2.setName("Euro");
        currencyDep2.setNominal(1);
        currencyDep2.setValue(new Value(BigDecimal.valueOf(0.9), date));

        // Act
        int affectedRows = currencyRepository.saveCurrencies(List.of(currencyDep1, currencyDep2));
        List<CurrencyDep> currencies = currencyRepository.getAllCurrency(date);

        // Assert
        assertEquals(2, affectedRows);
        assertNotNull(currencies);
        assertFalse(currencies.isEmpty());
    }

    @Test
    public void getAllCurrency_ShouldReturnEmptyList_WhenDateDoesNotExist() {
        // Arrange
        LocalDate date = LocalDate.of(2022, 1, 1); // An arbitrary date that does not exist in the database

        // Act
        List<CurrencyDep> currencies = currencyRepository.getAllCurrency(date);

        // Assert
        assertNotNull(currencies);
        assertTrue(currencies.isEmpty());
    }

    @Test
    public void getAllCurrenciesIds_ShouldReturnListOfIds() {
        // Arrange
        CurrencyDep currencyDep1 = new CurrencyDep();
        currencyDep1.setId("USD");
        currencyDep1.setName("US Dollar");
        currencyDep1.setNominal(1);
        currencyDep1.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        CurrencyDep currencyDep2 = new CurrencyDep();
        currencyDep2.setId("EUR");
        currencyDep2.setName("Euro");
        currencyDep2.setNominal(1);
        currencyDep2.setValue(new Value(BigDecimal.valueOf(0.8), LocalDate.now()));

        List<CurrencyDep> currencies = Arrays.asList(currencyDep1, currencyDep2);

        // Act
        int rowsAffected = currencyRepository.saveCurrencies(currencies);
        List<String> currencyIds = currencyRepository.getAllCurrenciesIds();

        // Assert
        assertEquals(2, rowsAffected);
        assertNotNull(currencyIds);
        assertFalse(currencyIds.isEmpty());
    }

    @Test
    public void isEmpty_ShouldReturnTrue_WhenCurrencyTableIsEmpty() {
        // Act
        boolean isEmpty = currencyRepository.isEmpty();

        // Assert
        assertTrue(isEmpty);
    }

    @Test
    public void saveCurrencies_ShouldReturnNumberOfRowsAffected_WhenSavedSuccessfully() {
        // Arrange
        CurrencyDep currencyDep1 = new CurrencyDep();
        currencyDep1.setId("USD");
        currencyDep1.setName("US Dollar");
        currencyDep1.setNominal(1);
        currencyDep1.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        CurrencyDep currencyDep2 = new CurrencyDep();
        currencyDep2.setId("EUR");
        currencyDep2.setName("Euro");
        currencyDep2.setNominal(1);
        currencyDep2.setValue(new Value(BigDecimal.valueOf(0.8), LocalDate.now()));

        List<CurrencyDep> currencies = Arrays.asList(currencyDep1, currencyDep2);

        // Act
        int rowsAffected = currencyRepository.saveCurrencies(currencies);

        // Assert
        assertTrue(rowsAffected > 0);
    }

    @Test
    public void updateCurrencies_ShouldReturnNumberOfRowsAffected_WhenUpdateSuccessful() {
        // Arrange
        CurrencyDep currencyDep1 = new CurrencyDep();
        currencyDep1.setId("USD");
        currencyDep1.setValue(new Value(BigDecimal.valueOf(1.2), LocalDate.now()));

        CurrencyDep currencyDep2 = new CurrencyDep();
        currencyDep2.setId("EUR");
        currencyDep2.setValue(new Value(BigDecimal.valueOf(0.9), LocalDate.now()));

        List<CurrencyDep> currencies = Arrays.asList(currencyDep1, currencyDep2);

        // Act
        int rowsAffected = currencyRepository.updateCurrencies(currencies);

        // Assert
        assertTrue(rowsAffected > 0);
    }

    @Test
    public void getCurrencyByPeriod_ShouldReturnListOfValues_WhenPeriodAndIdsExist() {
        // Arrange
        LocalDate startDate = LocalDate.of(2023, 1, 1);
        LocalDate endDate = LocalDate.of(2023, 6, 1);

        CurrencyDep currencyDep1 = new CurrencyDep();
        currencyDep1.setId("USD");
        currencyDep1.setName("US Dollar");
        currencyDep1.setNominal(1);
        currencyDep1.setValue(new Value(BigDecimal.valueOf(1.0), startDate));

        CurrencyDep currencyDep2 = new CurrencyDep();
        currencyDep2.setId("EUR");
        currencyDep2.setName("Euro");
        currencyDep2.setNominal(1);
        currencyDep2.setValue(new Value(BigDecimal.valueOf(0.8), endDate));


        String idFirst = "USD";
        String idSecond = "EUR";

        // Act
        int affectedRows = currencyRepository.saveCurrencies(List.of(currencyDep1, currencyDep2));
        List<CurrencyDep> values = currencyRepository.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);

        // Assert
        assertEquals(2, affectedRows);
        assertNotNull(values);
        assertFalse(values.isEmpty());
    }

    @Test
    public void getCurrencyByPeriod_ShouldReturnEmptyList_WhenPeriodOrIdsDoNotExist() {
        // Arrange
        LocalDate startDate = LocalDate.of(2022, 1, 1); // An arbitrary start date that does not exist in the database
        LocalDate endDate = LocalDate.of(2022, 6, 1); // An arbitrary end date that does not exist in the database
        String idFirst = "INVALID_ID";
        String idSecond = "INVALID_ID";

        // Act
        List<CurrencyDep> values = currencyRepository.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);

        // Assert
        assertNotNull(values);
        assertTrue(values.isEmpty());
    }

    @Test
    public void saveHistory_ShouldReturnId_WhenSavedSuccessfully() {
        // Arrange
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("100"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("85"));

        // Act
        int rowsAffected = currencyRepository.saveHistory(history);

        // Assert
        assertNotNull(rowsAffected);
        assertEquals(1, rowsAffected);
    }

    @Test
    public void saveHistory_ShouldThrowException_WhenSaveFails() {
        // Arrange
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("100"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("85"));

        // Create a mock JdbcTemplate that always returns 0 rows affected
        JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);
        when(mockJdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);

        CurrencyRepositoryImpl currencyRepository = new CurrencyRepositoryImpl(mockJdbcTemplate);
        // Act and Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(RuntimeException.class,
                () -> currencyRepository.saveHistory(history)
        );
        assertEquals("Failed to save history with id: " + history.getId(), exception.getMessage());
    }

    @Test
    public void getAllCurrencyHistory_ShouldReturnListOfHistory_WhenDataExists() {
        // Arrange
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("90"));
        history.setOutputCurrency("AED");
        history.setOutputAmount(new BigDecimal("85"));

        // Act
        currencyRepository.saveHistory(history);
        List<History> historyList = currencyRepository.getAllCurrencyHistory();

        // Assert
        assertNotNull(historyList);
        assertFalse(historyList.isEmpty());
        assertEquals(1, historyList.size());
    }

    @Test
    public void getAllCurrencyHistory_ShouldReturnEmptyList_WhenDataDoesNotExists() {
        // Act
        List<History> historyList = currencyRepository.getAllCurrencyHistory();

        // Assert
        assertNotNull(historyList);
        assertTrue(historyList.isEmpty());
    }

    @Test
    public void getCurrencyHistoryByInputCurrency_ShouldReturnListOfHistory_WhenInputCurrencyExists() {
        // Arrange
        String inputCurrency = "USD";
        // Create a history record1
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("60"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("65"));

        // Create a history record2
        History history1 = new History();
        history1.setId(2);
        history1.setConversionDate(LocalDate.now());
        history1.setInputCurrency("EUR");
        history1.setInputAmount(new BigDecimal("70"));
        history1.setOutputCurrency("USD");
        history1.setOutputAmount(new BigDecimal("75"));

        // Create a history record3
        History history2 = new History();
        history2.setId(3);
        history2.setConversionDate(LocalDate.now());
        history2.setInputCurrency("USD");
        history2.setInputAmount(new BigDecimal("80"));
        history2.setOutputCurrency("AED");
        history2.setOutputAmount(new BigDecimal("85"));

        // Act
        currencyRepository.saveHistory(history);
        currencyRepository.saveHistory(history1);
        currencyRepository.saveHistory(history2);
        List<History> historyFromDB = currencyRepository.getCurrencyHistoryByInputCurrency(inputCurrency);

        // Assert
        assertFalse(historyFromDB.isEmpty());
        assertEquals(2, historyFromDB.size());
        assertEquals(historyFromDB.get(0).getInputCurrency(), inputCurrency);
        assertEquals(historyFromDB.get(1).getInputCurrency(), inputCurrency);
    }

    @Test
    public void getCurrencyHistoryByInputCurrency_ShouldReturnEmptyList_WhenInputCurrencyDoesNotExists() {
        // Arrange
        String inputCurrency = "USD";
        // Create a history record1
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("AED");
        history.setInputAmount(new BigDecimal("60"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("65"));

        // Create a history record2
        History history1 = new History();
        history1.setId(2);
        history1.setConversionDate(LocalDate.now());
        history1.setInputCurrency("EUR");
        history1.setInputAmount(new BigDecimal("70"));
        history1.setOutputCurrency("USD");
        history1.setOutputAmount(new BigDecimal("75"));

        //Act
        currencyRepository.saveHistory(history);
        currencyRepository.saveHistory(history1);
        List<History> historyList = currencyRepository.getCurrencyHistoryByInputCurrency(inputCurrency);

        //Assert
        assertNotNull(historyList);
        assertTrue(historyList.isEmpty());
    }

    @Test
    public void getAllCurrencyHistoryByDate_ShouldReturnListOfHistoryForGivenDate_IfDateExists() {
        // Arrange
        LocalDate expectedDate = LocalDate.of(2022, 1, 1);

        // Create a history record1
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("60"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("65"));

        // Create a history record2
        History history1 = new History();
        history1.setId(2);
        history1.setConversionDate(LocalDate.of(2022, 1, 1));
        history1.setInputCurrency("EUR");
        history1.setInputAmount(new BigDecimal("70"));
        history1.setOutputCurrency("USD");
        history1.setOutputAmount(new BigDecimal("75"));

        // Create a history record3
        History history2 = new History();
        history2.setId(3);
        history2.setConversionDate(LocalDate.of(2022, 1, 1));
        history2.setInputCurrency("USD");
        history2.setInputAmount(new BigDecimal("80"));
        history2.setOutputCurrency("AED");
        history2.setOutputAmount(new BigDecimal("85"));

        // Act
        currencyRepository.saveHistory(history);
        currencyRepository.saveHistory(history1);
        currencyRepository.saveHistory(history2);
        List<History> historyList = currencyRepository.getAllCurrencyHistoryByDate(expectedDate);

        // Assert
        assertFalse(historyList.isEmpty());
        assertEquals(2, historyList.size());
        assertEquals(historyList.get(0).getConversionDate(), expectedDate);
        assertEquals(historyList.get(1).getConversionDate(), expectedDate);
    }

    @Test
    public void getAllCurrencyHistoryByDate_ShouldReturnEmptyList_IfDateDoesNotExists() {
        // Arrange
        LocalDate date = LocalDate.of(2022, 1, 1); // An arbitrary date that does not exist in the database

        // Create a history record1
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("60"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("65"));

        // Create a history record2
        History history1 = new History();
        history1.setId(2);
        history1.setConversionDate(LocalDate.now());
        history1.setInputCurrency("EUR");
        history1.setInputAmount(new BigDecimal("70"));
        history1.setOutputCurrency("USD");
        history1.setOutputAmount(new BigDecimal("75"));

        // Act
        currencyRepository.saveHistory(history);
        currencyRepository.saveHistory(history1);
        List<History> historyList = currencyRepository.getAllCurrencyHistoryByDate(date);

        // Assert
        assertNotNull(historyList);
        assertTrue(historyList.isEmpty());
    }

    @Test
    public void isEmptyHistory_ShouldReturnTrue_WhenHistoryTableIsEmpty() {
        // Act
        boolean isEmpty = currencyRepository.isEmptyHistory();

        // Assert
        assertTrue(isEmpty);
    }

    @Test
    public void clearHistory_ShouldDeleteAllEntriesFromHistory() {
        //Arrange
        History history = new History();
        history.setId(1);
        history.setConversionDate(LocalDate.now());
        history.setInputCurrency("USD");
        history.setInputAmount(new BigDecimal("60"));
        history.setOutputCurrency("EUR");
        history.setOutputAmount(new BigDecimal("65"));

        // Act
        int rowsAffected = currencyRepository.saveHistory(history);
        currencyRepository.clearHistory();

        // Assert
        assertNotNull(rowsAffected);
        assertTrue(currencyRepository.isEmptyHistory());
    }
}

