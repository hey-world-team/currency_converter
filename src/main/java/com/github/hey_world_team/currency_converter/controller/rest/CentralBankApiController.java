package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.service.CurrencyService;
import com.github.hey_world_team.currency_converter.service.FileService;
import com.github.hey_world_team.currency_converter.service.status.DataBasePrepare;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.time.LocalDate;
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
            fileService.prepareDataBase(DataBasePrepare.CREATE, null);
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

    @GetMapping(value = "/getAllCurrenciesByDate")
    public ResponseEntity<Collection<Currency>> getAllCurrenciesByDate(
            RequestEntity<?> entity,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("access to path {}", entity.getUrl());
        Collection<Currency> currencies = currencyService.getAllCurrency(date);
        return (currencies != null && !currencies.isEmpty())
                ? new ResponseEntity<>(currencies, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/getCurrencyByID/{currencyId}")
    public ResponseEntity<Currency> getCurrencyByID(
            @PathVariable(value = "currencyId") String currencyId) {
        log.info("access to API get currency cost by id: {}", currencyId);
        Currency currencyDto = currencyService.getCurrencyByID(currencyId);
        return (currencyDto != null)
                ? new ResponseEntity<>(currencyDto, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping(value = "/getCurrencyCost")
    public ResponseEntity<BigDecimal> getCurrencyCost(
            @RequestParam(value = "inputID") String inputID,
            @RequestParam("outputID") String outputID,
            @RequestParam("count") int count) {
        log.info("access to API get currency cost for: {} with count {} in {}", inputID, outputID, count);
        BigDecimal cost = currencyService.getCurrencyCost(inputID, outputID, count);
        return (cost != null)
                ? new ResponseEntity<>(cost, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/byPeriod")
    public ResponseEntity<List<Currency>> getCurrencyByPeriod(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam("currencyIdFirst") String idFirst,
            @RequestParam("currencyIdSecond") String idSecond) {
        List<Currency> currencyData = currencyService.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);
        return (!currencyData.isEmpty())
                ? new ResponseEntity<>(currencyData, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(value = "/prepareDataBase")
    public ResponseEntity<String> updateCurrencies(
            RequestEntity<?> entity,
            @RequestParam("status") String status,
            @RequestParam(value = "path", required = false) String path) {
        log.info("access to path {}", entity.getUrl());
        int affectedRows = 0;
        if (status.equals(DataBasePrepare.UPDATE.name())) {
            affectedRows = fileService.prepareDataBase(DataBasePrepare.UPDATE, path);
        } else if (status.equals(DataBasePrepare.CREATE.name())) {
            affectedRows = fileService.prepareDataBase(DataBasePrepare.CREATE, path);
        } else {
            throw new RuntimeException("Status is not handled");
        }

        return (affectedRows > 0)
                ? new ResponseEntity<>("Count of updated rows: " + affectedRows, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @Scheduled(cron = "0 1 1 * * ?")//every day on 01:01 pm
    public void onSchedule() {
        log.info("start schedule");
        int affectedRows = fileService.prepareDataBase(DataBasePrepare.UPDATE, null);
        log.info("end schedule, updated rows {}", affectedRows);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> exceptionHandlerNPE(Throwable npe) {
        String npeMessage = npe.getMessage();
        return new ResponseEntity<>(npeMessage, HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exceptionHandlerRuntime(Throwable runtimeErr) {
        String runTimeMessage = runtimeErr.getMessage();
        return new ResponseEntity<>(runTimeMessage, HttpStatus.NO_CONTENT);
    }
}
