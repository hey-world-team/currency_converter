package com.github.hey_world_team.currency_converter.model;

import lombok.*;

import java.math.BigDecimal;

/**
 * This class represents the best currency conversion rate using cryptocurrencies where convertedAmount- the best rate
 * you can get after conversion, conversionPath- full text information about a conversion
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class BestConversion {

    private BigDecimal convertedAmount;
    private String conversionPath;
}
