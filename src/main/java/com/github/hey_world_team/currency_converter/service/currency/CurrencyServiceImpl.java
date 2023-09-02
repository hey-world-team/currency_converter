package com.github.hey_world_team.currency_converter.service.currency;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import org.springframework.stereotype.Service;

@Service
public class CurrencyServiceImpl implements CurrencyService{

    @Override
    public boolean currencyIsActual(Chain chain) {
        return chain.isTcActual() && chain.isScActual();
    }

    @Override
    public long saveNewPrices(BestChainRequestContext bestChainRequestContext) {
        return 0;
    }
}
