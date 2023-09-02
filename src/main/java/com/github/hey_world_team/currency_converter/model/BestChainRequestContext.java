package com.github.hey_world_team.currency_converter.model;

import com.github.hey_world_team.currency_converter.service.exchange.ChoosedCoin;
import com.github.hey_world_team.currency_converter.service.currency.CurrencyType;
import com.github.hey_world_team.currency_converter.service.exchange.ChoosedExchange;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class BestChainRequestContext {

    private UUID id;
    private int countOfChains;
    private int decency;
    private boolean verified;
    private ChoosedExchange ce;
    private CurrencyType sc;
    private CurrencyType tc;
    private ChoosedCoin icc;
}
