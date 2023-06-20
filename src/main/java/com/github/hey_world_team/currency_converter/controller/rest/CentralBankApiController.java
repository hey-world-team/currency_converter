package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
public class CentralBankApiController {

    private static final Logger log = LoggerFactory.getLogger(CentralBankApiController.class);
    private final CurrencyService currencyService;

    //TODO for future api with current date
    //private static final String DATE_API = "date_req";

    @Autowired
    public CentralBankApiController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping(value = "/getAllIds")
    public ResponseEntity<List<String>> getAllCurrenciesId(RequestEntity<?> entity) {
        log.info("access to path {}", entity.getUrl());
        List<String> ids = currencyService.getAllCurrenciesId();
        return (ids != null && !ids.isEmpty())
                ? new ResponseEntity<>(ids, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/getAllCurrencies")
    public ResponseEntity<Collection<Currency>> getAllCurrencies(RequestEntity<?> entity) {
        log.info("access to path {}", entity.getUrl());
        Collection<Currency> currencies =  currencyService.getAllCurrency();
        return (currencies != null && !currencies.isEmpty())
                ? new ResponseEntity<>(currencies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/getCurrencyCost/{currencyId}")
    public ResponseEntity<Currency> getCurrencyCostById(
            @PathVariable(value = "currencyId") String currencyId) {
        log.info("access to API get currency cost by id: {}", currencyId);
        Currency currencyDto = currencyService.getCurrencyCost(currencyId);
        return (currencyDto != null)
                ? new ResponseEntity<>(currencyDto, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> exceptionHandler(Throwable npe) {
        String npeMessage = npe.getMessage();
        log.error(npeMessage);
        return new ResponseEntity<>(npeMessage, HttpStatus.NO_CONTENT);
    }
}
