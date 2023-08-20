package com.github.hey_world_team.currency_converter.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * This class  represents a value with decimal precision and its corresponding date
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Value {

    private BigDecimal value;
    private LocalDate date;
}
