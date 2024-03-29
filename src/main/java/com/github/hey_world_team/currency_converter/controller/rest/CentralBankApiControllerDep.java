package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.model.BestConversion;
import com.github.hey_world_team.currency_converter.model.CurrencyDep;
import com.github.hey_world_team.currency_converter.model.History;
import com.github.hey_world_team.currency_converter.service.processing.ConversionServiceDep;
import com.github.hey_world_team.currency_converter.service.currency.CurrencyServiceDep;
import com.github.hey_world_team.currency_converter.service.processing.FileService;
import com.github.hey_world_team.currency_converter.service.history.HistoryService;
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

/**
 * CentralBankApiController is controller REST API for interaction with currencies and history of conversion
 */
@RestController
@RequestMapping("/api/ver1/currency")
@Deprecated
public class CentralBankApiControllerDep {

    private static final Logger log = LoggerFactory.getLogger(CentralBankApiControllerDep.class);
    private final CurrencyServiceDep currencyServiceDep;
    private final FileService fileService;
    private final HistoryService historyService;
    private final ConversionServiceDep conversionServiceDep;

    //TODO for future api with current date
    //private static final String DATE_API = "date_req";

    @Autowired
    public CentralBankApiControllerDep(CurrencyServiceDep currencyServiceDep,
                                       FileService fileService,
                                       HistoryService historyService,
                                       ConversionServiceDep conversionServiceDep) {
        this.fileService = fileService;
        this.currencyServiceDep = currencyServiceDep;
        this.historyService = historyService;
        this.conversionServiceDep = conversionServiceDep;
    }

    /**
     * This method calls "prepareDataBase" method if  database is empty, executed after the class is instantiated
     */
    @PostConstruct
    public void onStartup() {
        if (currencyServiceDep.dbIsEmpty()) {
            fileService.prepareDataBase(DataBasePrepare.CREATE, null);
        }
    }

    /**
     * This method returns the history of currency conversion
     *
     * @return ResponseEntity with the collection of currency conversion history
     */
    @GetMapping(value = "/history")
    public ResponseEntity<Collection<History>> getAllHistory() {
        log.info("access to API get conversion history");
        Collection<History> history = historyService.getCurrencyHistory();
        return (history != null && !history.isEmpty())
               ? new ResponseEntity<>(history, HttpStatus.OK)
               : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method converts the specified amount from the source currency to the target currency using the given
     * conversion rates
     *
     * @param amount         The amount to convert
     * @param sourceCurrency The source currency code ("USD", "RUB" or "EUR")
     * @param costUSDT       The cost of 1 unit of the cryptocurrency USDT in the source currency
     * @param costETH        The cost of 1 unit of the cryptocurrency ETH in the source currency
     * @param costBTC        The cost of 1 unit of the cryptocurrency BTC in the source currency
     * @param targetCurrency The target currency code ("USD", "RUB" or "EUR")
     * @return ResponseEntity containing the best conversion result
     */
    @PostMapping(value = "/convert")
    public ResponseEntity<BestConversion> convertCurrency(
        @RequestParam("amount") BigDecimal amount,
        @RequestParam("sourceCurrency") String sourceCurrency,
        @RequestParam("costUSDT") BigDecimal costUSDT,
        @RequestParam("costETH") BigDecimal costETH,
        @RequestParam("costBTC") BigDecimal costBTC,
        @RequestParam("targetCurrency") String targetCurrency) {

        BestConversion result = conversionServiceDep.convertCurrency(amount,
                                                                     sourceCurrency,
                                                                     costUSDT,
                                                                     costETH,
                                                                     costBTC,
                                                                     targetCurrency);

        return (result != null)
               ? new ResponseEntity<>(result, HttpStatus.OK)
               : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method returns a list of identifiers for all currencies
     *
     * @param entity RequestEntity object representing the HTTP request
     * @return ResponseEntity with the list of currency identifiers
     */
    @GetMapping(value = "/getAllIds")
    public ResponseEntity<List<String>> getAllCurrenciesId(RequestEntity<?> entity) {
        log.info("access to path {}", entity.getUrl());
        List<String> ids = currencyServiceDep.getAllCurrenciesId();
        return (ids != null && !ids.isEmpty())
               ? new ResponseEntity<>(ids, HttpStatus.OK)
               : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method returns a collection of all currencies for the specified  date
     *
     * @param entity RequestEntity object representing the HTTP request
     * @param date   the date for which to retrieve the currencies
     * @return ResponseEntity with the collection of currencies for the specified  date
     */
    @GetMapping(value = "/getAllCurrenciesByDate")
    public ResponseEntity<Collection<CurrencyDep>> getAllCurrenciesByDate(
        RequestEntity<?> entity,
        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("access to path {}", entity.getUrl());
        Collection<CurrencyDep> currencies = currencyServiceDep.getAllCurrency(date);
        return (currencies != null && !currencies.isEmpty())
               ? new ResponseEntity<>(currencies, HttpStatus.OK)
               : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method returns the cost of a currency by its identifier
     *
     * @param currencyId the identifier of the currency
     * @return ResponseEntity with the currency information
     */
    @GetMapping(value = "/getCurrencyCost/{currencyId}")
    public ResponseEntity<CurrencyDep> getCurrencyCostById(
        @PathVariable(value = "currencyId") String currencyId) {
        log.info("access to API get currency cost by id: {}", currencyId);
        CurrencyDep currencyDepDto = currencyServiceDep.getCurrencyCost(currencyId);
        return (currencyDepDto != null)
               ? new ResponseEntity<>(currencyDepDto, HttpStatus.OK)
               : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method returns a list of currencies for the specified period and with the specified currency identifiers
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @param idFirst   the identifier of the first currency
     * @param idSecond  the identifier of the second currency
     * @return ResponseEntity with the list of currencies
     */
    @GetMapping("/byPeriod")
    public ResponseEntity<List<CurrencyDep>> getCurrencyByPeriod(
        @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
        @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
        @RequestParam("currencyIdFirst") String idFirst,
        @RequestParam("currencyIdSecond") String idSecond) {
        List<CurrencyDep> currencyDepData = currencyServiceDep.getCurrencyByPeriod(startDate, endDate, idFirst, idSecond);
        return (!currencyDepData.isEmpty())
               ? new ResponseEntity<>(currencyDepData, HttpStatus.OK)
               : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method updates  currencies data in the database
     *
     * @param entity RequestEntity object representing the HTTP request
     * @param status the status of the database update
     * @param path   the path to the file containing the updated data (optional)
     * @return ResponseEntity with information about the updating
     */
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

    /**
     * This method updates the database on a schedule and writes information about it into the log, is scheduled to run
     * automatically every day at 01:01 AM, it starts the database update process and logs the number of affected rows.
     * The database update is performed using the fileService's prepareDataBase method
     */
    @Scheduled(cron = "0 1 1 * * ?")//every day at 01:01 AM
    public void onSchedule() {
        log.info("start schedule");
        int affectedRows = fileService.prepareDataBase(DataBasePrepare.UPDATE, null);
        log.info("end schedule, updated rows {}", affectedRows);
    }

    /**
     * Exception handler for "NullPointerException"
     *
     * @param npe the thrown NullPointerException
     * @return ResponseEntity with error's message "NO_CONTENT"
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> exceptionHandlerNPE(Throwable npe) {
        String npeMessage = npe.getMessage();
        return new ResponseEntity<>(npeMessage, HttpStatus.NO_CONTENT);
    }

    /**
     * Exception handler for "RuntimeException"
     *
     * @param runtimeErr the thrown RuntimeException
     * @return ResponseEntity with error's message "NO_CONTENT"
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> exceptionHandlerRuntime(Throwable runtimeErr) {
        String runTimeMessage = runtimeErr.getMessage();
        return new ResponseEntity<>(runTimeMessage, HttpStatus.BAD_GATEWAY);
    }

    /**
     * Exception handler for "IllegalArgumentException"
     *
     * @param illegal the thrown IllegalArgumentException
     * @return ResponseEntity with error's message "NO_CONTENT"
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> exceptionHandlerIAE(Throwable illegal) {
        String illegalMessage = illegal.getMessage();
        return new ResponseEntity<>(illegalMessage, HttpStatus.BAD_REQUEST);
    }
}
