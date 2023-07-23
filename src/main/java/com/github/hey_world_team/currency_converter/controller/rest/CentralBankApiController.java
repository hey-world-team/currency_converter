package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.model.ConversionResponse;
import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.History;
import com.github.hey_world_team.currency_converter.service.ConversionService;
import com.github.hey_world_team.currency_converter.service.CurrencyService;
import com.github.hey_world_team.currency_converter.service.FileService;
import com.github.hey_world_team.currency_converter.service.HistoryService;
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
@RequestMapping("/api/currency")
public class CentralBankApiController {

    private static final Logger log = LoggerFactory.getLogger(CentralBankApiController.class);
    private final CurrencyService currencyService;
    private final FileService fileService;
    private final HistoryService historyService;
    private final ConversionService conversionService;

    //TODO for future api with current date
    //private static final String DATE_API = "date_req";

    @Autowired
    public CentralBankApiController(CurrencyService currencyService,
                                    FileService fileService,
                                    HistoryService historyService,
                                    ConversionService conversionService) {
        this.fileService = fileService;
        this.currencyService = currencyService;
        this.historyService = historyService;
        this.conversionService = conversionService;
    }

    /**
     * This method calls "prepareDataBase" method if  database is empty,
     * executed after the class is instantiated
     */
    @PostConstruct
    public void onStartup() {
        if (currencyService.dbIsEmpty()) {
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

    @PostMapping(value = "/convert")
    public ResponseEntity<ConversionResponse> convertCurrency(
            @RequestParam("amount") BigDecimal amount,
            @RequestParam("sourceCurrency") String sourceCurrency,
            @RequestParam("costUSDT") BigDecimal costUSDT,
            @RequestParam("costETH") BigDecimal costETH,
            @RequestParam("costBTC") BigDecimal costBTC,
            @RequestParam("targetCurrency") String targetCurrency) {

        ConversionResponse result = conversionService.convertCurrency(amount, sourceCurrency,costUSDT, costETH, costBTC, targetCurrency);

         return new ResponseEntity<>(result, HttpStatus.OK);
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
        List<String> ids = currencyService.getAllCurrenciesId();
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
    public ResponseEntity<Collection<Currency>> getAllCurrenciesByDate(
            RequestEntity<?> entity,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        log.info("access to path {}", entity.getUrl());
        Collection<Currency> currencies = currencyService.getAllCurrency(date);
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
    public ResponseEntity<Currency> getCurrencyCostById(
            @PathVariable(value = "currencyId") String currencyId) {
        log.info("access to API get currency cost by id: {}", currencyId);
        Currency currencyDto = currencyService.getCurrencyCost(currencyId);
        return (currencyDto != null)
                ? new ResponseEntity<>(currencyDto, HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * This method returns a list of currencies for the specified period
     * and with the specified currency identifiers
     *
     * @param startDate the start date of the period
     * @param endDate   the end date of the period
     * @param idFirst   the identifier of the first currency
     * @param idSecond  the identifier of the second currency
     * @return ResponseEntity with the list of currencies.
     */
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
     * This method updates the database on a schedule and writes information about it into the log,
     * is scheduled to run automatically every day at 01:01 AM,
     * it starts the database update process and logs the number of affected rows.
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
        return new ResponseEntity<>(runTimeMessage, HttpStatus.NO_CONTENT);
    }
}
