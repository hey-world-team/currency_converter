package com.github.hey_world_team.currency_converter.controller.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


@RestController
public class CentralBankApiController {
    @GetMapping(value = "/foreignCurrencies")
    public String getCurrencies() throws IOException {
        String link = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=02/03/2002";
        RestTemplate restTemplate = new RestTemplate();
        String currenciesXml = restTemplate.getForObject(link, String.class);
        writeToFile(currenciesXml);
        return currenciesXml;
    }

    public void writeToFile(String p) throws IOException {
        File currencyFile = new File("./src/main/resources/foreign_currencies.xml");
        try (FileOutputStream outputStream = new FileOutputStream(currencyFile, false)) {
            byte[] strToBytes = p.getBytes();
            outputStream.write(strToBytes);
        } catch (IOException ex) {
            Logger.getLogger(CentralBankApiController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}