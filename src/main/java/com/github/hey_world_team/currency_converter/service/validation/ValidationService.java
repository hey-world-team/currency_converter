package com.github.hey_world_team.currency_converter.service.validation;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

public interface ValidationService {
    boolean validateRequest(BestChainRequestContext requestContext) throws HttpClientErrorException.BadRequest;
    boolean chainIsActual(Chain chainList);
}
