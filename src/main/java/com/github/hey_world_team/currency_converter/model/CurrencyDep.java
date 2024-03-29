package com.github.hey_world_team.currency_converter.model;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

/**
 * This class represents a currency with its properties and value, POJO currency
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
@Deprecated
public class CurrencyDep {

    private String id;
    private String name;
    private Integer nominal;
    private Value value;
}
