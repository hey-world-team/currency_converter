package com.github.hey_world_team.currency_converter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class PropertiesForFileService {
    @Value("${filepath}")
    private String path;
    @Value("${link}")
    private String link;
    @Value("${file_name}")
    private String fileName;
    @Value("${charset}")
    private String charset;

    public String getPath() {
        return path;
    }

    public String getLink() {
        return link;
    }

    public String getFileForeignCurrencies() {
        return fileName;
    }

    public String getCharset() {
        return charset;
    }
}
