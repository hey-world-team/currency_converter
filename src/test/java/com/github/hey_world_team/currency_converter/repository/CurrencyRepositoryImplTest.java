package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.Currency;
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
@ContextConfiguration(classes = {CurrencyRepositoryImplTest.TestConfiguration.class})
public class CurrencyRepositoryImplTest {

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
                "DROP TABLE IF EXISTS currency CASCADE;";
        jdbcTemplate.execute(dropTableQuery);

        String createTableQuery = "CREATE TABLE currency (id VARCHAR(3) PRIMARY KEY, name VARCHAR(250) NOT NULL, nominal INT);\n" +
                "CREATE TABLE value (uuid UUID PRIMARY KEY DEFAULT uuid_generate_v4(), currency_id VARCHAR(3), value NUMERIC NOT NULL, date DATE NOT NULL, FOREIGN KEY (currency_id) REFERENCES currency(id));\n" +
                "CREATE EXTENSION IF NOT EXISTS \"uuid-ossp\";";
        jdbcTemplate.execute(createTableQuery);
    }

    @Test
    public void saveCurrency_ShouldReturnId_WhenSavedSuccessfully() {
        // Arrange
        Currency currency = new Currency();
        currency.setId("USD");
        currency.setName("US Dollar");
        currency.setNominal(1);
        currency.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act
        String id = currencyRepository.saveCurrency(currency);

        // Assert
        assertNotNull(id);
        assertEquals("USD", id);
    }

    @Test
    public void updateCurrency_ShouldReturnUpdatedCurrency_WhenUpdateSuccessful() {
        // Arrange
        Currency currency = new Currency();
        currency.setId("USD");
        currency.setName("US");
        currency.setNominal(1);
        currency.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        Currency currencyForUpdate = new Currency();
        currencyForUpdate.setId("USD");
        currencyForUpdate.setName("US Dollar");
        currencyForUpdate.setNominal(10);
        currencyForUpdate.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act
        String id = currencyRepository.saveCurrency(currency);
        Currency updatedCurrency = currencyRepository.updateCurrency(currencyForUpdate);

        // Assert
        assertNotNull(id);
        assertEquals("USD", id);
        assertNotNull(updatedCurrency);
        assertEquals(currencyForUpdate.getId(), updatedCurrency.getId());
        assertEquals(currencyForUpdate.getNominal(), updatedCurrency.getNominal());
        assertEquals(currencyForUpdate.getValue(), updatedCurrency.getValue());
        assertEquals(currencyForUpdate.getValue().getDate(), updatedCurrency.getValue().getDate());
    }

    @Test
    public void saveCurrencies_ShouldReturnRowCount_WhenSavedSuccessfully() {
        // Arrange
        Currency currency1 = new Currency();
        currency1.setId("USD");
        currency1.setName("US Dollar");
        currency1.setNominal(1);
        currency1.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        Currency currency2 = new Currency();
        currency2.setId("EUR");
        currency2.setName("Euro");
        currency2.setNominal(1);
        currency2.setValue(new Value(BigDecimal.valueOf(0.9), LocalDate.now()));

        List<Currency> currencies = Arrays.asList(currency1, currency2);

        // Act
        int rowCount = currencyRepository.saveCurrencies(currencies);

        // Assert
        assertEquals(2, rowCount);
    }

    @Test
    public void saveCurrency_ShouldThrowException_WhenSaveFails() {
        // Arrange
        Currency currency = new Currency();
        currency.setId("USD");
        currency.setName("US Dollar");
        currency.setNominal(1);
        currency.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Create a mock JdbcTemplate that always returns 0 rows affected
        JdbcTemplate mockJdbcTemplate = mock(JdbcTemplate.class);
        when(mockJdbcTemplate.update(anyString(), any(Object[].class))).thenReturn(0);

        CurrencyRepositoryImpl currencyRepository = new CurrencyRepositoryImpl(mockJdbcTemplate);
        // Act and Assert
        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
                RuntimeException.class,
                () -> currencyRepository.saveCurrency(currency)
        );
        assertEquals("Failed to save currency with id: USD", exception.getMessage());
    }

    @Test
    public void getCurrencyById_ShouldReturnCurrency_WhenIdExists() {
        // Arrange
        String id = "USD";

        Currency currency = new Currency();
        currency.setId(id);
        currency.setName("US Dollar");
        currency.setNominal(1);
        currency.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act
        String savedId = currencyRepository.saveCurrency(currency);
        Currency currencyFromDB = currencyRepository.getCurrencyById(id);

        // Assert
        assertNotNull(savedId);
        assertEquals(id, savedId);
        assertNotNull(currencyFromDB);
        assertEquals(id, currencyFromDB.getId());
    }

    @Test
    public void getCurrencyById_ShouldReturnNull_WhenIdDoesNotExist() {
        // Arrange
        String id = "INVALID_ID";

        // Act
        Currency currency = currencyRepository.getCurrencyById(id);

        // Assert
        assertNull(currency);
    }

    @Test
    public void updateCurrency_ShouldThrowException_WhenUpdateFails() {
        // Arrange
        Currency currency = new Currency();
        currency.setId("INVALID_ID");
        currency.setName("Invalid Currency");
        currency.setNominal(1);
        currency.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        // Act and Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> currencyRepository.updateCurrency(currency)
        );
        assertEquals("Failed to update currency with id: " + currency.getId(), exception.getMessage());
    }

    @Test
    public void getAllCurrency_ShouldReturnListOfCurrencies_WhenDateExists() {
        // Arrange
        LocalDate date = LocalDate.now();

        Currency currency1 = new Currency();
        currency1.setId("USD");
        currency1.setName("US Dollar");
        currency1.setNominal(1);
        currency1.setValue(new Value(BigDecimal.valueOf(1.0),date));

        Currency currency2 = new Currency();
        currency2.setId("EUR");
        currency2.setName("Euro");
        currency2.setNominal(1);
        currency2.setValue(new Value(BigDecimal.valueOf(0.9), date));

        // Act
        int affectedRows = currencyRepository.saveCurrencies(List.of(currency1, currency2));
        List<Currency> currencies = currencyRepository.getAllCurrency(date);


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
        List<Currency> currencies = currencyRepository.getAllCurrency(date);

        // Assert
        assertNotNull(currencies);
        assertTrue(currencies.isEmpty());
    }

    @Test
    public void getAllCurrenciesIds_ShouldReturnListOfIds() {
        // Arrange
        Currency currency1 = new Currency();
        currency1.setId("USD");
        currency1.setName("US Dollar");
        currency1.setNominal(1);
        currency1.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        Currency currency2 = new Currency();
        currency2.setId("EUR");
        currency2.setName("Euro");
        currency2.setNominal(1);
        currency2.setValue(new Value(BigDecimal.valueOf(0.8), LocalDate.now()));

        List<Currency> currencies = Arrays.asList(currency1, currency2);

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
        Currency currency1 = new Currency();
        currency1.setId("USD");
        currency1.setName("US Dollar");
        currency1.setNominal(1);
        currency1.setValue(new Value(BigDecimal.valueOf(1.0), LocalDate.now()));

        Currency currency2 = new Currency();
        currency2.setId("EUR");
        currency2.setName("Euro");
        currency2.setNominal(1);
        currency2.setValue(new Value(BigDecimal.valueOf(0.8), LocalDate.now()));

        List<Currency> currencies = Arrays.asList(currency1, currency2);

        // Act
        int rowsAffected = currencyRepository.saveCurrencies(currencies);

        // Assert
        assertTrue(rowsAffected > 0);
    }

    @Test
    public void updateCurrencies_ShouldReturnNumberOfRowsAffected_WhenUpdateSuccessful() {
        // Arrange
        Currency currency1 = new Currency();
        currency1.setId("USD");
        currency1.setValue(new Value(BigDecimal.valueOf(1.2), LocalDate.now()));

        Currency currency2 = new Currency();
        currency2.setId("EUR");
        currency2.setValue(new Value(BigDecimal.valueOf(0.9), LocalDate.now()));

        List<Currency> currencies = Arrays.asList(currency1, currency2);

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

        Currency currency1 = new Currency();
        currency1.setId("USD");
        currency1.setName("US Dollar");
        currency1.setNominal(1);
        currency1.setValue(new Value(BigDecimal.valueOf(1.0), startDate));

        Currency currency2 = new Currency();
        currency2.setId("EUR");
        currency2.setName("Euro");
        currency2.setNominal(1);
        currency2.setValue(new Value(BigDecimal.valueOf(0.8), endDate));


        String idFirst = "USD";
        String idSecond = "EUR";

        // Act
int affectedRows = currencyRepository.saveCurrencies(List.of(currency1, currency2));
        List<Currency> values = currencyRepository.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);

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
        List<Currency> values = currencyRepository.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);

        // Assert
        assertNotNull(values);
        assertTrue(values.isEmpty());
    }
}