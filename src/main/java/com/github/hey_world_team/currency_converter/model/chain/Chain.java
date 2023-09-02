package com.github.hey_world_team.currency_converter.model.chain;

import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Slf4j
@Builder
public class Chain {
    private UUID id;
    private String exchangeName;
    private List<Order> orderList;
    private boolean tcActual;
    private boolean scActual;
    private boolean scToIccActual;
    private boolean iccToTcActual;
}
