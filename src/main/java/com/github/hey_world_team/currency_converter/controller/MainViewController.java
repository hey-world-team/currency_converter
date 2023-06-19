package com.github.hey_world_team.currency_converter.controller;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.service.CurrencyService;
import com.github.hey_world_team.currency_converter.service.FileService;
import com.github.hey_world_team.currency_converter.service.statuses.FileWriteStatus;
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
    private final FileService fileService;
    private final String link;
    private final CurrencyService currencyService;

    @Autowired
    public MainViewController(FileService fileService,
                              PropertiesForFileService properties,
                              CurrencyService currencyService) {
        this.fileService = fileService;
        this.link = properties.getLink();
        this.currencyService = currencyService;
    }

    @PostConstruct
    public void onStartup() {
        prepareDataBase();
    }

    @Scheduled(cron = "0 1 1 * * ?")//every day on 01:01 pm
    public void onSchedule() {
        log.info("start schedule");
        prepareDataBase();
        log.info("end schedule");
    }

    @GetMapping("/")
    public ModelAndView mainPage() {
        ModelAndView modelAndView = new ModelAndView("index");
        String date = LocalDate.now().format(format);
        modelAndView.addObject("date", date);
        modelAndView.addObject("currentCourse", currencyService.getCurrentCourse());
        return modelAndView;
    }

    @GetMapping("/history")
    public String historyPage() {
        return "index";
    }

    @GetMapping("/availableCryptosExchange")
    public String availableCryptosExchangePage() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "index";
    }

    @GetMapping("/signin")
    public String signinPage() {
        return "index";
    }

    private void prepareDataBase() {
        log.info("start prepare data base");
        var restTemplate = new RestTemplate();
        String currenciesXml = restTemplate.getForObject(link, String.class);
        log.info("download from {} current course", link);
        assert currenciesXml != null;
        String answer = fileService.writeToFile(currenciesXml);
        log.info("current course {}", answer);
        if (answer.equals(FileWriteStatus.WRITTEN.name())) {
            log.info("start parsing to data base");
            fileService.parseXmlToObject();
            log.info("end parsing to data base");
        }
        log.info("end prepare data base");
    }
}
