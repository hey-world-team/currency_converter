package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(CurrencyService.class);
    private final CurrencyRepository repository;

    @Autowired
    public CurrencyService(CurrencyRepository currencyDataRepository) {
        this.repository = currencyDataRepository;
    }

    public Currency getCurrencyByID(String currencyId) {
        log.info("getting currency cost by currency id: {}", currencyId);
        return repository.getCurrencyById(currencyId);
    }

    public List<String> getAllCurrenciesId() {
        log.info("getting currency ids");
        return repository.getAllCurrenciesIds();
    }

    public Collection<Currency> getAllCurrency(LocalDate date) {
        return repository.getAllCurrency(date)
                .stream()
                .map(currency -> Currency.builder()
                        .id(currency.getId())
                        .value(currency.getValue())
                        .name(currency.getName())
                        .nominal(currency.getNominal())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean dbIsEmpty() {
        return repository.isEmpty();
    }

    public List<Currency> getCurrencyByPeriod(LocalDate startDate, LocalDate endDate, String idFIrst, String idSecond) {
        return repository.getCurrencyByPeriod(startDate, endDate, idFIrst, idSecond);
    }

    public BigDecimal getCurrencyCost(String inputID, String outputID, int count) {
        Currency inputCurrency = repository.getCurrencyById(inputID);
        Currency outputCurrency = repository.getCurrencyById(outputID);

        BigDecimal first;
        BigDecimal second;
        BigDecimal result;
        if ("RUB".equals(inputCurrency.getId())) {
            result = BigDecimal.valueOf(1)
                    .divide(outputCurrency.getValue().getValue(), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(outputCurrency.getNominal()))
                    .multiply(BigDecimal.valueOf(count));
        } else if ("RUB".equals(outputCurrency.getId())) {
            result = inputCurrency.getValue().getValue()
                    .multiply(BigDecimal.valueOf(count))
                    .divide(BigDecimal.valueOf(inputCurrency.getNominal()), MathContext.DECIMAL128);
        } else {
            first = inputCurrency.getValue().getValue()
                    .multiply(BigDecimal.valueOf(count))
                    .divide(BigDecimal.valueOf(inputCurrency.getNominal()), MathContext.DECIMAL128);
            second = BigDecimal.valueOf(1)
                    .divide(outputCurrency.getValue().getValue(), MathContext.DECIMAL128)
                    .multiply(BigDecimal.valueOf(outputCurrency.getNominal()));
            result = first.multiply(second);
        }
        return result;
    }
}
