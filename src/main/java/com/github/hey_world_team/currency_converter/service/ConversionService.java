package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.model.ConversionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ConversionService {

    private final Map<String, BigDecimal> cryptoRatesToUsd;
    private final Map<String, BigDecimal> cryptoRatesToRub;

    @Autowired
    public ConversionService(CurrencyService service) {
        // Rub 100000
        cryptoRatesToUsd = new LinkedHashMap<>();
        cryptoRatesToUsd.put("USDT", new BigDecimal("1.01")); // Курс USDT к USD
        cryptoRatesToUsd.put("ETH", new BigDecimal("1880")); // Курс ETH к USD
        cryptoRatesToUsd.put("BTC", new BigDecimal("29800")); // Курс BTC к USD
        //USD 10000
        cryptoRatesToRub = new LinkedHashMap<>();
        cryptoRatesToRub.put("USDT", new BigDecimal("92")); // Курс USDT к RUB
        cryptoRatesToRub.put("ETH", new BigDecimal("173000")); // Курс ETH к RUB
        cryptoRatesToRub.put("BTC", new BigDecimal("2456000")); // Курс BTC к RUB
    }

    public ConversionResponse convertCurrency(
            BigDecimal amount,
            String sourceCurrency,
            BigDecimal costUSDT,
            BigDecimal costETH,
            BigDecimal costBTC,
            String targetCurrency) {
        ConversionResponse response = new ConversionResponse();

        // Проверяем, соответствует ли sourceCurrency одному из значений USD, RUB или EURO
        if (!Arrays.asList("USD", "RUB", "EUR").contains(sourceCurrency.toUpperCase())) {
            throw new IllegalArgumentException("Invalid sourceCurrency. Only USD, RUB, or EUR are allowed.");
        }
        List<BigDecimal> cost = new ArrayList<>();
        cost.add(costUSDT);
        cost.add(costETH);
        cost.add(costBTC);

        String bestCrypto;
        BigDecimal bestRate = BigDecimal.ZERO;

        int i = 0;
        for (String crypto : cryptoRatesToUsd.keySet()) {
            BigDecimal convertedAmount = amount.divide(cost.get(i), 4, RoundingMode.HALF_EVEN)
                    .multiply(cryptoRatesToUsd.get(crypto));

            // Сравниваем convertedAmount с текущим bestRate
            int compareResult = convertedAmount.compareTo(bestRate);

            // Если convertedAmount больше bestRate, обновляем bestRate
            if (compareResult > 0) {
                bestRate = convertedAmount;
                bestCrypto = crypto;
                response.setConversionPath(String.format("The best way from %s to %s across %s will be costs %s", sourceCurrency, targetCurrency, bestCrypto, bestRate));
                response.setConvertedAmount(bestRate);
            }
            i++;
        }
        return response;
    }
}