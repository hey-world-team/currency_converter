package com.github.hey_world_team.currency_converter.model.chain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class Order {
    private long id;
    private Trader trader;
    private Trade trade;
    private boolean isBuy;
    private boolean isSell;
    private long dateTime;
}
