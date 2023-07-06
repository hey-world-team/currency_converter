package com.github.hey_world_team.currency_converter.model;

import lombok.*;


import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *  This class represents a currency conversion history record
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class History {

    private int id;
    private LocalDate conversionDate;
    private String inputCurrency;
    private BigDecimal inputAmount;
    private String outputCurrency;
    private BigDecimal outputAmount;
}