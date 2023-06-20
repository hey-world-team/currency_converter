package com.github.hey_world_team.currency_converter.entity;

import lombok.*;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "currency")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
@ToString
@Builder
public class Currency {
    @Id
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "value")
    private BigDecimal value;
    @Column(name = "nominal")
    private Integer nominal;
    @Column(name = "description")
    private String description;
}
