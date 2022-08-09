package com.github.hey_world_team.currency_converter.dto;

import java.math.BigDecimal;
import java.util.Objects;

public class Currency {
    private String id;
    private String name;
    private BigDecimal value;
    private Integer nominal;

    public Currency() {
    }

    public Currency(String id, String name, BigDecimal value, Integer nominal) {
        this.id = id;
        this.name = name;
        this.value = value;
        this.nominal = nominal;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    public Integer getNominal() {
        return nominal;
    }

    public void setNominal(Integer nominal) {
        this.nominal = nominal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Currency)) return false;
        Currency currency = (Currency) o;
        return Objects.equals(id, currency.id) && Objects.equals(name, currency.name) && Objects.equals(value, currency.value) && Objects.equals(nominal, currency.nominal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, value, nominal);
    }

    @Override
    public String toString() {
        return "Currency{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", value=" + value +
                ", nominal=" + nominal +
                '}';
    }
}
