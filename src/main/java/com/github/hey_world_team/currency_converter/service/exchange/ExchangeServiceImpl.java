package com.github.hey_world_team.currency_converter.service.exchange;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import org.springframework.stereotype.Service;

@Service
public class ExchangeServiceImpl implements ExchangeService {

    @Override
    public boolean chainIsActual(Chain chain) {
        return chain.isScToIccActual() && chain.isIccToTcActual();
    }

    @Override
    public long saveNewOrderList(BestChainRequestContext bestChainRequestContext) {
        return 0;
    }
}
