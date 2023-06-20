package com.github.hey_world_team.currency_converter.dto;

import java.math.BigDecimal;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * POJO currency
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class CurrencyDto {

    private String id;
    private String name;
    private BigDecimal value;
    private Integer nominal;
}
