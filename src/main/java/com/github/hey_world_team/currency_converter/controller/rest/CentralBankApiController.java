package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.service.CurrencyService;
import com.github.hey_world_team.currency_converter.service.FileService;
import com.github.hey_world_team.currency_converter.service.status.DataBasePrepare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
public class CentralBankApiController {

    private static final Logger log = LoggerFactory.getLogger(CentralBankApiController.class);
    private final CurrencyService currencyService;
    private final FileService fileService;

    //TODO for future api with current date
    //private static final String DATE_API = "date_req";

    @Autowired
    public CentralBankApiController(CurrencyService currencyService,
                                    FileService fileService) {
        this.fileService = fileService;
        this.currencyService = currencyService;
    }

    @PostConstruct
    public void onStartup() {
        if (currencyService.dbIsEmpty()) {
            fileService.prepareDataBase(DataBasePrepare.CREATE);
        } {
            fileService.prepareDataBase(DataBasePrepare.UPDATE);
        }
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

    @PostMapping(value = "/updateCurrencies")
    public ResponseEntity<String> updateCurrencies(RequestEntity<?> entity) {
        log.info("access to path {}", entity.getUrl());
        int affectedRows = fileService.prepareDataBase(DataBasePrepare.UPDATE);
        return (affectedRows >  0)
                ? new ResponseEntity<>("Count of updated rows: " + affectedRows, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Scheduled(cron = "0 1 1 * * ?")//every day on 01:01 pm
    public void onSchedule() {
        log.info("start schedule");
        int affectedRows = fileService.prepareDataBase(DataBasePrepare.UPDATE);
        log.info("end schedule, updated rows {}", affectedRows);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> exceptionHandler(Throwable npe) {
        String npeMessage = npe.getMessage();
        log.error(npeMessage);
        return new ResponseEntity<>(npeMessage, HttpStatus.NO_CONTENT);
    }
}
