package com.github.hey_world_team.currency_converter.repository;

import com.github.hey_world_team.currency_converter.dto.CurrencyDto;
import com.github.hey_world_team.currency_converter.entity.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.*;

@Repository
public class CurrencyDataRepository implements CurrencyRepository {

    private static final Logger log = LoggerFactory.getLogger(CurrencyDataRepository.class);
    private final EntityManager entityManager;

    @Autowired
    public CurrencyDataRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public String saveCurrency(Currency currency) {
        log.info("save currency id: {}, name: {}, cost: {}",
                currency.getId(),
                currency.getName(),
                currency.getValue());
        entityManager.persist(currency);
        entityManager.flush();
        return currency.getId();
    }

    @Override
    public Currency getCurrencyById(String id) {
        if (entityManager.contains(id)) {
            log.info("currency with id {} found", id);
            Currency currency = entityManager.find(Currency.class, id);
            return currency;
        } else {
            log.info("currency with id {} NOT found", id);
            return null;
        }
    }

    @Override
    public Currency updateCurrency(Currency currency) {
        return null;
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<Currency> getAllCurrency() {
        return entityManager.createQuery("select c from Currency c ").getResultList();
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<String> getAllCurrenciesIds() {
        return entityManager.createQuery("select c.id from Currency c ").getResultList();
    }
}
