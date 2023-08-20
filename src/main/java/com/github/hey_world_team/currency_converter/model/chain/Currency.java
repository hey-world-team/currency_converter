package com.github.hey_world_team.currency_converter.model.chain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class Currency {
    private String id;
    private String bankName;
    private String currencyName;
    private BigDecimal price;
    private long dateTime;
}
