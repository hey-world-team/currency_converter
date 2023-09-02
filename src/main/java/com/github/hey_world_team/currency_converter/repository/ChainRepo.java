package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;

public interface ChainRepo {
    Chain getActualChainByRequest(BestChainRequestContext requestContext);
}
