package com.github.hey_world_team.currency_converter.service.processing;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import com.github.hey_world_team.currency_converter.service.chain.ChainService;
import com.github.hey_world_team.currency_converter.service.currency.CurrencyService;
import com.github.hey_world_team.currency_converter.service.exchange.ExchangeService;
import com.github.hey_world_team.currency_converter.service.validation.ValidationService;
import com.github.hey_world_team.currency_converter.service.validation.ValidationServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.springframework.web.client.HttpClientErrorException.*;

@Service
public class ConverterProcessor {

    private final ValidationService validationService;
    private final ChainService chainService;
    private final CurrencyService currencyService;
    private final ExchangeService exchangeService;

    @Autowired
    public ConverterProcessor(ValidationServiceImpl validationService,
                              ChainService chainService,
                              CurrencyService currencyService,
                              ExchangeService exchangeService) {
        this.validationService = validationService;
        this.chainService = chainService;
        this.currencyService = currencyService;
        this.exchangeService = exchangeService;
    }

    public Chain startProcessing(BestChainRequestContext bestChainRequestContext) throws BadRequest {

        if (validationService.validateRequest(bestChainRequestContext)) {
            Chain chain = chainService.getActualChainByRequest(bestChainRequestContext);
            if (validationService.chainIsActual(chain)) {
                return chain;
            } else {
                if (!currencyService.currencyIsActual(chain)) {
                    // TODO (rowCount = 0) retry
                    long rowCount = currencyService.saveNewPrices(bestChainRequestContext);
                }
                if (!exchangeService.chainIsActual(chain)) {
                    // TODO (rowCount = 0) retry
                    long rowCount = exchangeService.saveNewOrderList(bestChainRequestContext);
                }
            }
            return chainService.getActualChainByRequest(bestChainRequestContext);
        } else {
            throw new RuntimeException("Validation request failed");
        }
    }
}
