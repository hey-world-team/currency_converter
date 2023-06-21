package com.github.hey_world_team.currency_converter.controller;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.service.CurrencyService;
import com.github.hey_world_team.currency_converter.service.FileService;
import com.github.hey_world_team.currency_converter.service.status.FileWriteStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
public class MainViewController {

    private static final Logger log = LoggerFactory.getLogger(MainViewController.class);
    final private DateTimeFormatter format = DateTimeFormatter.ofPattern("d.MM.yyyy");
    private final CurrencyService currencyService;

    @Autowired
    public MainViewController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @GetMapping("/")
    public ModelAndView mainPage() {
        log.info("Request to /");
        ModelAndView modelAndView = new ModelAndView("index");
        String date = LocalDate.now().format(format);
        modelAndView.addObject("date", date);
        modelAndView.addObject("currentCourse", currencyService.getAllCurrency());
        return modelAndView;
    }

    @GetMapping("/history")
    public String historyPage() {
        log.info("Request to /history");
        return "index";
    }

    @GetMapping("/availableCryptosExchange")
    public String availableCryptosExchangePage() {
        log.info("Request to /availableCryptosExchange");
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        log.info("Request to /login");
        return "index";
    }

    @GetMapping("/signin")
    public String signinPage() {
        log.info("Request to /signin");
        return "index";
    }
}
