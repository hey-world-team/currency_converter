package com.github.hey_world_team.currency_converter.model.chain;

import com.github.hey_world_team.currency_converter.service.currency.CurrencyType;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class Trade {
    private long id;
    private String coinICC;
    private CurrencyType currency;
    private BigDecimal price;
    private boolean isBuy;
    private boolean isSell;

}
