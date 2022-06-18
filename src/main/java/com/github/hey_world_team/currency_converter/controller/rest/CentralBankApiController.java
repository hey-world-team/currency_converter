package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;


@RestController
public class CentralBankApiController {

    private final Logger log = LoggerFactory.getLogger(CentralBankApiController.class);
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
    public ResponseEntity<String> getCurrencies() {
        RestTemplate restTemplate = new RestTemplate();
        String currenciesXml = restTemplate.getForObject(link, String.class);
        String answer = fileService.writeToFile(currenciesXml);
        return new ResponseEntity<>("File was " + answer.toLowerCase(), HttpStatus.OK);
    }

    //Для тестирования методов
    @GetMapping(value = "/test")
    public ResponseEntity<String> createObject() throws IOException {
//        String answer = fileService.parseXmlToObject();
//        return new ResponseEntity<>("File was " + answer.toLowerCase(), HttpStatus.OK);
        log.info("Link: {}", link);
        return new ResponseEntity<>(link, HttpStatus.OK);
    }
}