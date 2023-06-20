package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.dto.CurrencyDto;
import com.github.hey_world_team.currency_converter.entity.Currency;
import com.github.hey_world_team.currency_converter.repository.CurrencyDataRepository;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyDataRepository) {
        this.repository = currencyDataRepository;
    }

    public CurrencyDto getCurrencyCost(String currencyId) {
        log.info("getting currency cost by currency id: {}", currencyId);
        Currency currency = repository.getCurrencyById(currencyId);
        CurrencyDto currencyDto = CurrencyDto.builder()
                .id(currency.getId())
                .name(currency.getName())
                .value(currency.getValue())
                .nominal(currency.getNominal())
                .build();
        return currencyDto;
    }

    public List<String> getAllCurrenciesId() {
        log.info("getting currency ids");
        return repository.getAllCurrenciesIds();
    }

    public Collection<CurrencyDto> getCurrentCourse() {
        return repository.getAllCurrency().stream().map(x -> CurrencyDto.builder()
                .id(x.getId())
                .value(x.getValue())
                .nominal(x.getNominal())
                .build()).collect(Collectors.toList());
    }
}
