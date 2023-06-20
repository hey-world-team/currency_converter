package com.github.hey_world_team.currency_converter.model;

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
public class Currency {

    private String id;
    private String name;
    private BigDecimal value;
    private Integer nominal;
}
