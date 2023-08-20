package com.github.hey_world_team.currency_converter.service.chain;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;

import java.util.List;

public interface ChainService {
        Chain getActualChainByRequest(BestChainRequestContext requestContext);
}
