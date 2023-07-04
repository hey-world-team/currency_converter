package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.model.Currency;
import com.github.hey_world_team.currency_converter.model.Value;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepository;
import com.github.hey_world_team.currency_converter.service.status.DataBasePrepare;
import com.github.hey_world_team.currency_converter.service.status.FileWriteStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


/**
 * FileService is responsible for reading and writing data to a file,
 * preparing data for saving to a database
 */
@Service
public class FileService {

    private static final String CURRENCY_TAG_NAME = "Valute";

    private static final Logger log = LoggerFactory.getLogger(FileService.class);
    private final String fileForeignCurrencies;
    private final String charset;
    private final String link;
    private final PropertiesForFileService propertiesForFileService;
    private final CurrencyRepository currencyRepository;
    private final List<Currency> currencies;

    @Autowired
    public FileService(PropertiesForFileService propertiesForFileService,
                       CurrencyRepository currencyRepository) {
        this.propertiesForFileService = propertiesForFileService;
        this.fileForeignCurrencies = propertiesForFileService.getFileForeignCurrencies();
        this.charset = propertiesForFileService.getCharset();
        this.link = propertiesForFileService.getLink();
        this.currencyRepository = currencyRepository;
        this.currencies = new ArrayList<>();
    }

    /**
     * This method prepare database depending on the passed status and file path
     *
     * @param status the status indicates how the database should be prepared
     * @param path   the file path to the XML data
     * @return the number of records prepared in the DB
     */
    public int prepareDataBase(DataBasePrepare status, String path) {
        log.info("start prepare data base");
        if (path == null) {
            prepareCollectionByDownloadFile();
        } else {
            prepareCollectionFromFile(path);
        }
        return preparingDB(status);
    }

    /**
     * This method loads data from an external source and parses the XML file
     */
    private void prepareCollectionByDownloadFile() {
        var restTemplate = new RestTemplate();
        String currenciesXml = restTemplate.getForObject(link, String.class);
        log.info("download from {} current course", link);
        assert currenciesXml != null;
        String answer = writeToFile(currenciesXml);
        log.info("current course {}", answer);
        if (answer.equals(FileWriteStatus.WRITTEN.name())) {
            log.info("start parsing data to collection");
            parseXmlToCollectionObjects(null);
            log.info("end parsing data to  collection");
        }
    }

    /**
     * This method parses an XML file from the specified path
     *
     * @param path the file path to the XML data
     */
    private void prepareCollectionFromFile(String path) {
        log.info("start parsing data to collection");
        parseXmlToCollectionObjects(path);
        log.info("end parsing data to  collection");
    }

    /**
     * This method adds or updates records in the database depending on the status
     *
     * @param status the status indicating the operation to be performed
     * @return the number of records prepared in the DB
     */
    private int preparingDB(DataBasePrepare status) {
        int count = 0;
        if (status.equals(DataBasePrepare.CREATE)) {
            log.info("data base is empty need to create new records");
            this.currencies.add(new Currency("RUB", "Российский рубль", 1, new Value(null, LocalDate.now())));
            count = currencyRepository.saveCurrencies(currencies);
        } else if (status.equals(DataBasePrepare.UPDATE)) {
            log.info("data base is not empty need to update values");
            count = currencyRepository.updateCurrencies(currencies);
        } else {
            throw new RuntimeException("Data base preparing status isn't understandable: " + status);
        }
        log.info("end prepare data base");
        return count;
    }

    /**
     * This method writes the passed file to the specified path.
     *
     * @param file the file to be written
     * @return the result of operation, an answer to the controller
     */
    public String writeToFile(String file) {
        log.info("Started to read file {}", fileForeignCurrencies);
        var currencyFile = new File(propertiesForFileService.getPath() + "/" + fileForeignCurrencies);
        try (var outputStream = new FileOutputStream(currencyFile, false)) {
            log.info("Started to write file {}", fileForeignCurrencies);
            outputStream.write(file.getBytes());
        } catch (IOException ex) {
            log.error(ex.getMessage());
            return FileWriteStatus.NOT_WRITTEN.name();
        }
        log.info("Write {} completed", fileForeignCurrencies);
        return FileWriteStatus.WRITTEN.name();
    }

    /**
     * This method parses the XML file into a collection of Currency objects
     *
     * @param path the file path to the XML data
     */
    public void parseXmlToCollectionObjects(String path) {
        log.info("Started writing XML to object");
        Document doc = null;
        if (path == null) {
            var input = new File(propertiesForFileService.getPath() + "/" + fileForeignCurrencies);

            try {
                doc = Jsoup.parse(input, charset, "", Parser.xmlParser());
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        } else {
            var input = new File(path + "/" + fileForeignCurrencies);
            try {
                doc = Jsoup.parse(input, charset, "", Parser.xmlParser());
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new RuntimeException(e);
            }
        }

        String dateAttribute = doc.select("ValCurs").attr("Date");
        LocalDate date = LocalDate.parse(dateAttribute, DateTimeFormatter.ofPattern("d.MM.yyyy"));
        for (Element e : doc.select(CURRENCY_TAG_NAME)) {
            String id = e.getElementsByTag("CharCode").text();
            String name = e.getElementsByTag("Name").text();
            BigDecimal value = BigDecimal.valueOf(Double.parseDouble(e.getElementsByTag("Value")
                    .text()
                    .replace(',', '.')));
            Integer nominal = Integer.valueOf(e.getElementsByTag("Nominal").text());
            Value currencyValue = new Value(value, date);
            Currency currency = new Currency(id, name, nominal, currencyValue);
            this.currencies.add(currency);
        }
    }
}
