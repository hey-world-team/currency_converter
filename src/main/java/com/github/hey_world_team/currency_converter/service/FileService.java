package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.repository.CurrencyDataRepository;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import static java.lang.Double.parseDouble;

@Service
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);
    private static final String FILE_FOREIGN_CURRENCIES = "foreign_currencies.xml";
    private final PropertiesForFileService propertiesForFileService;
    private final CurrencyDataRepository currencyDataRepository;

    @Autowired
    public FileService(PropertiesForFileService propertiesForFileService, CurrencyDataRepository currencyDataRepository) {
        this.propertiesForFileService = propertiesForFileService;
        this.currencyDataRepository = currencyDataRepository;
    }

    public String writeToFile(String file) {
        logger.info("Started read file {}", FILE_FOREIGN_CURRENCIES);
        File currencyFile = new File(propertiesForFileService.getPath() + FILE_FOREIGN_CURRENCIES);
        try (FileOutputStream outputStream = new FileOutputStream(currencyFile, false)) {
            byte[] strToBytes = file.getBytes();
            logger.info("Started write file {}", FILE_FOREIGN_CURRENCIES);
            outputStream.write(strToBytes);
        } catch (IOException ex) {
            logger.error(ex.getMessage());
            return FileWriteStatus.NOT_WRITTEN.name();
        }
        logger.info("Write {} completed", FILE_FOREIGN_CURRENCIES);
        return FileWriteStatus.WRITTEN.name();
    }

    public String parseXmlToObject() throws IOException {
        logger.info("Started writing XML to object");
        File input = new File(propertiesForFileService.getPath() + FILE_FOREIGN_CURRENCIES);
        Document doc = Jsoup.parse(input, "windows-1251", "", Parser.xmlParser());

        for (Element e : doc.select("Valute")) {
            String name = e.getElementsByTag("Name").text();
            Double value = parseDouble(e.getElementsByTag("Value").text().replace(',', '.'));
            currencyDataRepository.save(name, value);
        }
        return XmlParseStatus.PARSED.name() + "\n---\nContent:\n" + currencyDataRepository.getAllCurrencies().toString();
    }
}
