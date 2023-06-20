package com.github.hey_world_team.currency_converter.model;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Value {
    private String id;
    private BigDecimal value;
    private LocalDate date;
}
