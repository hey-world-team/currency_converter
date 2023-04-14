package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.dto.CurrencyDto;
import com.github.hey_world_team.currency_converter.repository.CurrencyDataRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
public class CurrencyService {

  private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
  CurrencyDataRepository currencyDataRepository;

  @Autowired
  public CurrencyService(CurrencyDataRepository currencyDataRepository) {
    this.currencyDataRepository = currencyDataRepository;
  }

  public CurrencyDto getCurrencyCost(String currencyId) {
    log.info("getting currency cost by currency id: {}", currencyId);
    return currencyDataRepository.getCurrencyValueById(currencyId);
  }

  public List<String> getAllCurrenciesId() {
    log.info("getting currency ids");
    return currencyDataRepository.getAllCurrenciesId();
  }

  public Collection<CurrencyDto> getCurrentCourse() {
    return currencyDataRepository.getAllCurrencies();
  }
}
