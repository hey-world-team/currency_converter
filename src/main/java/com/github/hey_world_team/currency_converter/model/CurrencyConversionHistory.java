package com.github.hey_world_team.currency_converter.model;

import lombok.*;


import java.math.BigDecimal;
import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class CurrencyConversionHistory {

    private long id;
    private Date conversionDate;
    private String inputCurrency;
    private BigDecimal inputAmount;
    private String outputCurrency;
    private BigDecimal outputAmount;
}