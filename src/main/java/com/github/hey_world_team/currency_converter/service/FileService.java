package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.dto.CurrencyDto;
import com.github.hey_world_team.currency_converter.entity.Currency;
import com.github.hey_world_team.currency_converter.repository.CurrencyDataRepository;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepository;
import com.github.hey_world_team.currency_converter.service.statuses.FileWriteStatus;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

import static java.lang.Double.parseDouble;

@Service
@Transactional
public class FileService {

  private static final String VALUTE_TAG_NAME = "Valute";

  private static final Logger log = LoggerFactory.getLogger(FileService.class);
  private final String fileForeignCurrencies;
  private final String charset;
  private final PropertiesForFileService propertiesForFileService;
  private final CurrencyRepository repository;

  @Autowired
  public FileService(PropertiesForFileService propertiesForFileService,
                     CurrencyRepository currencyDataRepository) {
    this.propertiesForFileService = propertiesForFileService;
    this.repository = currencyDataRepository;
    this.fileForeignCurrencies = propertiesForFileService.getFileForeignCurrencies();
    this.charset = propertiesForFileService.getCharset();
  }

  /**
   * @param file
   * @return answer to controller
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
   *
   */
  public void parseXmlToObject() {
    log.info("Started writing XML to object");
    var input = new File(propertiesForFileService.getPath() + "/" + fileForeignCurrencies);
    Document doc = null;
    try {
      doc = Jsoup.parse(input, charset, "", Parser.xmlParser());
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new RuntimeException(e);
    }

    for (Element e : doc.select(VALUTE_TAG_NAME)) {
      String id = e.getElementsByTag("CharCode").text();
      String name = e.getElementsByTag("Name").text();
      BigDecimal value = BigDecimal.valueOf(parseDouble(e.getElementsByTag("Value")
                                                         .text()
                                                         .replace(',', '.')));
      Integer nominal = Integer.valueOf(e.getElementsByTag("Nominal").text());
      Currency currency = new Currency(id, name, value, nominal, "");

      repository.saveCurrency(currency);
    }
  }
}
