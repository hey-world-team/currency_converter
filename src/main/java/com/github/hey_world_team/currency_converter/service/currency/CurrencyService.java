package com.github.hey_world_team.currency_converter.service.currency;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import com.github.hey_world_team.currency_converter.service.processing.ConverterProcessor;

import java.util.Currency;
import java.util.List;

public interface CurrencyService {

    boolean currencyIsActual(Chain chain);
    long saveNewPrices(BestChainRequestContext bestChainRequestContext);
}
