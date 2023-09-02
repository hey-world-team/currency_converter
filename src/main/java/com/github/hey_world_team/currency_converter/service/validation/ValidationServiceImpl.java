package com.github.hey_world_team.currency_converter.service.validation;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import org.springframework.stereotype.Service;

@Service
public class ValidationServiceImpl implements ValidationService{

    @Override
    public boolean validateRequest(BestChainRequestContext requestContext) {
        //TODO add more checks
        return requestContext.getTc() != null && requestContext.getSc() != null;
    }

    @Override
    public boolean chainIsActual(Chain chain) {
        return chain.isIccToTcActual() && chain.isScActual() && chain.isTcActual() && chain.isScToIccActual();
    }
}
