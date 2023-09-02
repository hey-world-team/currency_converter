package com.github.hey_world_team.currency_converter.controller.rest;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.Chain;
import com.github.hey_world_team.currency_converter.service.exchange.ChoosedCoin;
import com.github.hey_world_team.currency_converter.service.exchange.ChoosedExchange;
import com.github.hey_world_team.currency_converter.service.currency.CurrencyType;
import com.github.hey_world_team.currency_converter.service.processing.ConverterProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Controller
@RequestMapping("/api/ver2/currency")
public class CurrencyConverterApi {

    private final ConverterProcessor processor;

    @Autowired
    public CurrencyConverterApi(ConverterProcessor processor) {
        this.processor = processor;
    }

    private static final Logger log = LoggerFactory.getLogger(CurrencyConverterApi.class);

    /**
     * Method provides a list of best chains for exchange from source currency (SC) to target currency (TC) through a
     * p2p buy/sell intermediate crypto coins (ICC) in the crypto exchange (CE).
     *
     * @param countOfChains count of chains in answer
     * @param decency       % successfully trades of trader
     * @param verified      check by only verified traders
     * @param ce            what crypto exchange do you want to use by default all
     * @param sc            source currency  RUB/EUR/USD
     * @param tc            target currency  RUB/EUR/USD
     * @param icc           intermediate crypto coins USDT/BTC/ETH
     * @return Provides a list of chains of the best way to converting by: buy ICC for SC -> buy TC for ICC
     */
    @GetMapping(value = "/getBestChain")
    public ResponseEntity<Chain> getBestChainList(
        @RequestParam(value = "countOfChains", required = false, defaultValue = "20") int countOfChains,
        @RequestParam(value = "decency", required = false, defaultValue = "98") int decency,
        @RequestParam(value = "verified", required = false, defaultValue = "false") boolean verified,
        @RequestParam(value = "cryptoExchange", required = false) String ce,
        @RequestParam(value = "sourceCurrency") String sc,
        @RequestParam(value = "targetCurrency") String tc,
        @RequestParam(value = "intermediateCryptoCoins", required = false) String icc) {
        BestChainRequestContext requestContext
            = BestChainRequestContext.builder()
                                     .id(UUID.randomUUID())
                                     .countOfChains(countOfChains)
                                     .decency(decency)
                                     .verified(verified)
                                     .ce((ce != null && ChoosedExchange.contains(ce))
                                         ? ChoosedExchange.valueOf(ce)
                                         : ChoosedExchange.ALL)
                                     .sc((CurrencyType.contains(sc)) ? CurrencyType.valueOf(sc) : null)
                                     .tc((CurrencyType.contains(tc)) ? CurrencyType.valueOf(tc) : null)
                                     .icc((ChoosedCoin.contains(icc)) ? ChoosedCoin.valueOf(icc) : ChoosedCoin.ALL)
                                     .build();
        return ResponseEntity.ok(processor.startProcessing(requestContext));
    }
}
