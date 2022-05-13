package com.github.hey_world_team.currency_converter;

import com.github.hey_world_team.currency_converter.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CurrencyConverterApplication {
    @Autowired
    static FileService service;

    public static void main(String[] args) {

        SpringApplication.run(CurrencyConverterApplication.class, args);
    }
}