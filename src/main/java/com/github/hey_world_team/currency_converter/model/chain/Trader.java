package com.github.hey_world_team.currency_converter.model.chain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class Trader {
    //info about trader
    private boolean isBuyer;
    private boolean isSeller;
}
