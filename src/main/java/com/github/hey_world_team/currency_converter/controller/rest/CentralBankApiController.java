package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.Locale;


@RestController
public class CentralBankApiController {

    private static final Logger log = LoggerFactory.getLogger(CentralBankApiController.class);
    private final FileService fileService;
    private final String link;

    @Autowired
    public CentralBankApiController(FileService fileService,
                                    PropertiesForFileService propertiesForFileService) {
        this.fileService = fileService;
        this.link = propertiesForFileService.getLink();
    }

    //TODO for future api with current date
    //private static final String DATE_API = "date_req";

    @GetMapping(value = "/foreignCurrencies")
    public ResponseEntity<String> getCurrencies(RequestEntity<?> entity) {
        log.info("access to path {}", entity.getUrl());
        var restTemplate = new RestTemplate();
        String currenciesXml = restTemplate.getForObject(link, String.class);
        assert currenciesXml != null;
        String answer = fileService.writeToFile(currenciesXml);
        return new ResponseEntity<>("File was " + answer.toLowerCase(Locale.ENGLISH), HttpStatus.OK);
    }

    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<String> exceptionHandler(Throwable npe) {
        String npeMessage = npe.getMessage();
        log.error(npeMessage);
        return new ResponseEntity<>(npeMessage, HttpStatus.NO_CONTENT);
    }
}
