package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@RestController
public class CentralBankApiController {
    private final FileService fileService;

    @Autowired
    public CentralBankApiController(FileService fileService) {
        this.fileService = fileService;
    }

    //TODO for future api with current date
    //private static final String DATE_API = "date_req";
    private static final String LINK = "http://www.cbr.ru/scripts/XML_daily.asp";

    @GetMapping(value = "/foreignCurrencies")
    public ResponseEntity<String> getCurrencies() {
        RestTemplate restTemplate = new RestTemplate();
        String currenciesXml = restTemplate.getForObject(LINK, String.class);
        String answer = fileService.writeToFile(currenciesXml);
        return new ResponseEntity<>("File was " + answer.toLowerCase(), HttpStatus.OK);
    }

    //Для тестирования методов
    @GetMapping(value = "/test")
    public ResponseEntity<String> createObject() throws IOException {
        String answer = fileService.parseXmlToObject();
        return new ResponseEntity<>("File was " + answer.toLowerCase(), HttpStatus.OK);
    }
}