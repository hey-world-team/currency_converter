package com.github.hey_world_team.currency_converter.repository.mock;

import com.github.hey_world_team.currency_converter.model.BestChainRequestContext;
import com.github.hey_world_team.currency_converter.model.chain.*;
import com.github.hey_world_team.currency_converter.repository.ChainRepo;
import com.github.hey_world_team.currency_converter.service.currency.CurrencyType;
import com.github.hey_world_team.currency_converter.service.exchange.ChoosedCoin;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Component
public class ChainRepoMock {

    public Chain getActualChainByRequest(BestChainRequestContext requestContext) {
        Trader buyer = new Trader(true, false);
        Trader seller = new Trader(false, true);

        Trade buy = new Trade(1L, ChoosedCoin.USDT.name(), CurrencyType.RUB, new BigDecimal(105), true, false);
        Trade sell = new Trade(2L, ChoosedCoin.USDT.name(), CurrencyType.EUR, new BigDecimal(0.92), false, true);

        return Chain.builder()
                           .id(UUID.randomUUID())
                           .exchangeName("BANANZE")
                           .orderList(List.of(Order.builder()
                                                   .id(1L)
                                                   .trader(buyer)
                                                   .trade(buy)
                                                   .isBuy(true)
                                                   .isSell(false)
                                                   .dateTime(System.currentTimeMillis()).build(),
                                              Order.builder()
                                                   .id(2L)
                                                   .trader(seller)
                                                   .trade(sell)
                                                   .isBuy(false)
                                                   .isSell(true)
                                                   .dateTime(System.currentTimeMillis()).build()))
                           .tcActual(true)
                           .scActual(true)
                           .iccToTcActual(true)
                           .scToIccActual(true)
                           .build();
    }

    public Chain getChainByRequestCurrencyNotActual(BestChainRequestContext requestContext) {
        Trader buyer = new Trader(true, false);
        Trader seller = new Trader(false, true);

        Trade buy = new Trade(1L, ChoosedCoin.USDT.name(), CurrencyType.RUB, new BigDecimal(105), true, false);
        Trade sell = new Trade(2L, ChoosedCoin.USDT.name(), CurrencyType.EUR, new BigDecimal(0.92), false, true);

        return Chain.builder()
                    .id(UUID.randomUUID())
                    .exchangeName("BANANZE")
                    .orderList(List.of(Order.builder()
                                            .id(1L)
                                            .trader(buyer)
                                            .trade(buy)
                                            .isBuy(true)
                                            .isSell(false)
                                            .dateTime(System.currentTimeMillis()).build(),
                                       Order.builder()
                                            .id(2L)
                                            .trader(seller)
                                            .trade(sell)
                                            .isBuy(false)
                                            .isSell(true)
                                            .dateTime(System.currentTimeMillis()).build()))
                    .tcActual(false)
                    .scActual(false)
                    .iccToTcActual(true)
                    .scToIccActual(true)
                    .build();
    }

    public Chain getChainByRequestCoinsNotActual(BestChainRequestContext requestContext) {
        Trader buyer = new Trader(true, false);
        Trader seller = new Trader(false, true);

        Trade buy = new Trade(1L, ChoosedCoin.USDT.name(), CurrencyType.RUB, new BigDecimal(105), true, false);
        Trade sell = new Trade(2L, ChoosedCoin.USDT.name(), CurrencyType.EUR, new BigDecimal(0.92), false, true);

        return Chain.builder()
                    .id(UUID.randomUUID())
                    .exchangeName("BANANZE")
                    .orderList(List.of(Order.builder()
                                            .id(1L)
                                            .trader(buyer)
                                            .trade(buy)
                                            .isBuy(true)
                                            .isSell(false)
                                            .dateTime(System.currentTimeMillis()).build(),
                                       Order.builder()
                                            .id(2L)
                                            .trader(seller)
                                            .trade(sell)
                                            .isBuy(false)
                                            .isSell(true)
                                            .dateTime(System.currentTimeMillis()).build()))
                    .tcActual(true)
                    .scActual(true)
                    .iccToTcActual(false)
                    .scToIccActual(false)
                    .build();
    }

    public Chain getNotActualChainByRequest(BestChainRequestContext requestContext) {
        Trader buyer = new Trader(true, false);
        Trader seller = new Trader(false, true);

        Trade buy = new Trade(1L, ChoosedCoin.USDT.name(), CurrencyType.RUB, new BigDecimal(105), true, false);
        Trade sell = new Trade(2L, ChoosedCoin.USDT.name(), CurrencyType.EUR, new BigDecimal(0.92), false, true);

        return Chain.builder()
                    .id(UUID.randomUUID())
                    .exchangeName("BANANZE")
                    .orderList(List.of(Order.builder()
                                            .id(1L)
                                            .trader(buyer)
                                            .trade(buy)
                                            .isBuy(true)
                                            .isSell(false)
                                            .dateTime(System.currentTimeMillis()).build(),
                                       Order.builder()
                                            .id(2L)
                                            .trader(seller)
                                            .trade(sell)
                                            .isBuy(false)
                                            .isSell(true)
                                            .dateTime(System.currentTimeMillis()).build()))
                    .tcActual(false)
                    .scActual(false)
                    .iccToTcActual(false)
                    .scToIccActual(false)
                    .build();
    }

}
