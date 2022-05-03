package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@Service
public class FileService {
    private final Logger logger = LoggerFactory.getLogger(FileService.class);
    private final String FILE_FOREIGN_CURRENCIES = "foreign_currencies.xml";
    private final PropertiesForFileService propertiesForFileService;

    @Autowired
    public FileService(PropertiesForFileService propertiesForFileService) {
        this.propertiesForFileService = propertiesForFileService;
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
}
