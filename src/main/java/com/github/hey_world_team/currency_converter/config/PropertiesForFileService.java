package com.github.hey_world_team.currency_converter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class PropertiesForFileService {
    @Value( "${filepath}" )
    private String path;

    public String getPath() {
        return path;
    }
}
