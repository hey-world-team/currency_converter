package com.github.hey_world_team.currency_converter.model;

import lombok.*;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class ConversionHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
     private Long id;

    private Date conversionDate;
    private String inputCurrency;
    private BigDecimal inputAmount;
    private String outputCurrency;
    private BigDecimal outputAmount;

}