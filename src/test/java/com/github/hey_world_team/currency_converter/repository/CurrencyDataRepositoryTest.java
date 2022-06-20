package com.github.hey_world_team.currency_converter.repository;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

public class CurrencyDataRepositoryTest {

    @org.junit.Before
    public void setUp() throws Exception {
    }

    @org.junit.Test
    public void save() {
        CurrencyDataRepository repository = new CurrencyDataRepository();
        assertEquals(0,repository.getAllCurrencies().size());
        repository.save("Ruble", 1.5);
        assertEquals(1,repository.getAllCurrencies().size());
        assertEquals(new Double(1.5),repository.getCurrencyValueByName("Ruble"));
    }

    @org.junit.Test
    public void getCurrencyValueByName() {
        CurrencyDataRepository repository = new CurrencyDataRepository();
        assertNull(repository.getCurrencyValueByName("Ruble"));
        repository.save("Ruble", 1.5);
        assertNotNull(repository.getCurrencyValueByName("Ruble"));
        assertEquals(new Double(1.5),repository.getCurrencyValueByName("Ruble"));
    }

    @org.junit.Test
    public void getAllCurrencies() {
        CurrencyDataRepository repository = new CurrencyDataRepository();
        assertEquals(new HashMap<>(),repository.getAllCurrencies());
        repository.save("Ruble", 1.5);
        assertNotEquals(new HashMap<>(),repository.getAllCurrencies());
        repository.save("Dollar", 10.0);
        repository.save("Euro", 10.9);
        assertEquals(3,repository.getAllCurrencies().size());
    }

    @org.junit.Test
    public void updateCurrencyValueByName() {
        CurrencyDataRepository repository = new CurrencyDataRepository();
        repository.save("Ruble", 1.5);
        repository.save("Euro", 10.9);
        assertEquals(new Double(1.5),repository.getCurrencyValueByName("Ruble"));
        assertEquals(new Double(10.9),repository.getCurrencyValueByName("Euro"));
        repository.updateCurrencyValueByName("Ruble", 2.0);
        assertEquals(new Double(2.0),repository.getCurrencyValueByName("Ruble"));
        assertEquals(new Double(10.9),repository.getCurrencyValueByName("Euro"));
    }

    @Test
    public void updateCurrencyValues() {
        CurrencyDataRepository repository = new CurrencyDataRepository();
        Map<String, Double> newRepository = new HashMap<>();
        repository.save("Ruble", 1.5);
        repository.save("Euro", 10.9);
        repository.save("Dollar", 10.0);
        assertEquals(3,repository.getAllCurrencies().size());
        assertEquals(new Double(1.5),repository.getCurrencyValueByName("Ruble"));
        assertEquals(new Double(10.9),repository.getCurrencyValueByName("Euro"));
        assertEquals(new Double(10),repository.getCurrencyValueByName("Dollar"));
        assertNull(repository.getCurrencyValueByName("Zloty"));
        newRepository.put("Ruble", 1.5);
        newRepository.put("Euro", 9.67);
        newRepository.put("Zloty", 5.9);
        newRepository.put("Dollar", 9.123);
        repository.updateCurrencyValues(newRepository);
        assertEquals(4,repository.getAllCurrencies().size());
        assertEquals(new Double(1.5),repository.getCurrencyValueByName("Ruble"));
        assertEquals(new Double(9.67),repository.getCurrencyValueByName("Euro"));
        assertEquals(new Double(5.9),repository.getCurrencyValueByName("Zloty"));
        assertEquals(new Double(9.123),repository.getCurrencyValueByName("Dollar"));
    }
}