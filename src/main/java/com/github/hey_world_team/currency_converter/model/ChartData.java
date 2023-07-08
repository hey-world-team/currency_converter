package com.github.hey_world_team.currency_converter.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class ChartData {
    private LocalDate date;
    private BigDecimal rubValue;
    private BigDecimal usdValue;
}

