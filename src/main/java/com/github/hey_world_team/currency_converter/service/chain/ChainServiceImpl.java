package com.github.hey_world_team.currency_converter.service.chain;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import com.github.hey_world_team.currency_converter.repository.mock.ChainRepoMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChainServiceImpl implements ChainService {

    private final ChainRepoMock chainRepoMock;

    @Autowired
    public ChainServiceImpl(ChainRepoMock chainRepo) {this.chainRepoMock = chainRepo;}

    @Override
    public Chain getActualChainByRequest(BestChainRequestContext requestContext) {
//        return chainRepo.getActualChainByRequest(requestContext);
        return chainRepoMock.getNotActualChainByRequest(requestContext);
    }
}
