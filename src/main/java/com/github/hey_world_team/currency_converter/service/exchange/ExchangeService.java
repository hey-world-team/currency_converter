package com.github.hey_world_team.currency_converter.service.exchange;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import com.github.hey_world_team.currency_converter.service.currency.CurrencyType;

public interface ExchangeService {
    boolean chainIsActual(Chain chainList);
    long saveNewOrderList(BestChainRequestContext bestChainRequestContext);
}
