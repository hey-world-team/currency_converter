package com.github.hey_world_team.currency_converter.model;

import lombok.*;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class BestConversion {
    private BigDecimal convertedAmount;
    private String conversionPath;
}
