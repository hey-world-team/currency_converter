package com.github.hey_world_team.currency_converter.model;

import lombok.*;


import java.math.BigDecimal;
import java.util.Date;

/**
 *  This class represents a currency conversion history record
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class History {

    private long id;
    private Date conversionDate;
    private String inputCurrency;
    private BigDecimal inputAmount;
    private String outputCurrency;
    private BigDecimal outputAmount;
}