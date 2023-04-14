package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.dto.CurrencyDto;
import java.math.BigDecimal;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class CurrencyDataRepositoryTest {

  private CurrencyDataRepository repository;
  private CurrencyDto currencyDto;

  @Before
  public void prepareCurrency() {
    this.repository = new CurrencyDataRepository();
    this.currencyDto = new CurrencyDto("test", "test", new BigDecimal(10), 10);
  }

  @After
  public void clearRepo() {
    repository.deleteAllCurrencies();
  }

  @Test
  public void saveCurrency() {
    assertEquals("There is should be 0 currencies, because we dons save anything",
                 0,
                 repository.getAllCurrencies().size());
    repository.save(currencyDto);
    assertEquals("There is should be 1, because we save one currency - Ruble",
                 1,
                 repository.getAllCurrencies().size());
    assertEquals("Price of test 10",
                 currencyDto.getValue(),
                 repository.getCurrencyValueById("test").getValue());
  }

  @Test
  public void getCurrencyValueById() {
    assertNull("We dont have currency test", repository.getCurrencyValueById("test"));
    repository.save(currencyDto);
    assertNotNull(repository.getCurrencyValueById("test"));
    assertEquals(currencyDto, repository.getCurrencyValueById("test"));
  }

  @Test
  public void getAllCurrencies() {
    assertEquals(0, repository.getAllCurrencies().size());
    repository.save(currencyDto);
    currencyDto.setId("test1");
    repository.save(currencyDto);
    currencyDto.setId("test2");
    repository.save(currencyDto);
    assertEquals(3, repository.getAllCurrencies().size());
  }
}