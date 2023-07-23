package com.github.hey_world_team.currency_converter.model;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Response {
    private BigDecimal convertedAmount;
    private List<String> conversionPath;
}
