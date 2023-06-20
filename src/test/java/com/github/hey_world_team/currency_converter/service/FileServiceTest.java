package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.config.PropertiesForFileService;
import com.github.hey_world_team.currency_converter.repository.CurrencyRepositoryImpl;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import static org.mockito.Mockito.mock;

public class FileServiceTest {
    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();//provide check for our mocks

    @InjectMocks//here we point where we want to inject our mock's dependency
    private FileService fileService;

    @Mock
    PropertiesForFileService propertiesForFileService;

    @Mock
    CurrencyRepositoryImpl currencyRepositoryImpl;

    @Test
    public void writeToFile() {

    }

    @Test
    public void parseXmlToObject() {
    }
}