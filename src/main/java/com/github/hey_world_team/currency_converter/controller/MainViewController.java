package com.github.hey_world_team.currency_converter.controller;

import com.github.hey_world_team.currency_converter.service.CurrencyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * MainViewController is the controller for handling HTTP requests in the application It is responsible for displaying
 * various pages and transferring data between the client and the server
 */
@Controller
public class MainViewController {

    private static final Logger log = LoggerFactory.getLogger(MainViewController.class);
    final private DateTimeFormatter format = DateTimeFormatter.ofPattern("d.MM.yyyy");
    private final CurrencyService currencyService;

    @Autowired
    public MainViewController(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    /**
     * This method  handles GET request along the "/" path, creates an  ModelAndView object with view "index", gets
     * current date and list of all currencies by method "getAllCurrency" and  adds it to the model as the attribute
     *
     * @return a ModelAndView object
     */
    @GetMapping("/")
    public ModelAndView mainPage() {
        log.info("Request to /");
        ModelAndView modelAndView = new ModelAndView("index");
        String date = LocalDate.now().format(format);
        modelAndView.addObject("date", date);
        modelAndView.addObject("currentCourse", currencyService.getAllCurrency(LocalDate.now()));
        return modelAndView;
    }

    /**
     * This method handles GET request along the path "/history"
     *
     * @return a string representing a history of past requests
     */
    @GetMapping("/history")
    public String historyPage() {
        log.info("Request to /history");
        return "index";
    }

    /**
     * This method handles GET request along the path "/availableCryptosExchange"
     *
     * @return a string representing a list of available cryptocurrencies
     */
    @GetMapping("/availableCryptosExchange")
    public String availableCryptosExchangePage() {
        log.info("Request to /availableCryptosExchange");
        return "index";
    }

    /**
     * This method handles GET request along the path "/login"
     *
     * @return a string representing a login of some user
     */
    @GetMapping("/login")
    public String loginPage() {
        log.info("Request to /login");
        return "index";
    }

    /**
     * This method handles GET request along the path "/signin"
     *
     * @return a string representing a registration of new user
     */
    @GetMapping("/signin")
    public String signinPage() {
        log.info("Request to /signin");
        return "index";
    }
}
