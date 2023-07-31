package com.github.hey_world_team.currency_converter.service;

import com.github.hey_world_team.currency_converter.model.BestConversion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Service
public class ConversionService {

    //TODO replace these maps with real repository
    //temporary solution for storing data about cryptocurrencies
    private final Map<String, BigDecimal> cryptoRatesToUsd;
    private final Map<String, BigDecimal> cryptoRatesToRub;
    private final Map<String, BigDecimal> cryptoRatesToEur;

    @Autowired
    public ConversionService(CurrencyService service) {
        cryptoRatesToUsd = new LinkedHashMap<>();
        cryptoRatesToUsd.put("USDT", new BigDecimal("1.01")); // USDT to USD exchange rate
        cryptoRatesToUsd.put("ETH", new BigDecimal("1880")); // ETH to USD exchange rate
        cryptoRatesToUsd.put("BTC", new BigDecimal("29800")); // BTC to USD exchange rate

        cryptoRatesToRub = new LinkedHashMap<>();
        cryptoRatesToRub.put("USDT", new BigDecimal("92")); // USDT to RUB exchange rate
        cryptoRatesToRub.put("ETH", new BigDecimal("173000")); // ETH to RUB exchange rate
        cryptoRatesToRub.put("BTC", new BigDecimal("2456000")); // BTC to RUB exchange rate

        cryptoRatesToEur = new LinkedHashMap<>();
        cryptoRatesToEur.put("USDT", new BigDecimal("99")); // USDT to EUR exchange rate
        cryptoRatesToEur.put("ETH", new BigDecimal("183000")); // ETH to EUR exchange rate
        cryptoRatesToEur.put("BTC", new BigDecimal("2656000")); // BTC to EUR exchange rate
    }

    public BestConversion convertCurrency(BigDecimal amount,
                                          String sourceCurrency,
                                          BigDecimal costUSDT,
                                          BigDecimal costETH,
                                          BigDecimal costBTC,
                                          String targetCurrency) {
        BestConversion response;
        List<BigDecimal> costList = new ArrayList<>();
        costList.add(costUSDT);
        costList.add(costETH);
        costList.add(costBTC);

        // Checking if sourceCurrency corresponds to one of the values USD, RUB, or EURO
        if (!Arrays.asList("USD", "RUB", "EUR").contains(sourceCurrency.toUpperCase()) ||
                !Arrays.asList("USD", "RUB", "EUR").contains(targetCurrency.toUpperCase()) ||
                sourceCurrency.equalsIgnoreCase(targetCurrency)) {
            throw new IllegalArgumentException("Invalid sourceCurrency or targetCurrency. Only USD, RUB, or EUR are allowed.");
        }
        response = findBestCryptoPrice(amount, costList, sourceCurrency, targetCurrency);
        return response;
    }

    private BestConversion findBestCryptoPrice(BigDecimal amount,
                                               List<BigDecimal> costList,
                                               String sourceCurrency,
                                               String targetCurrency) {
        BestConversion result = new BestConversion();

        Map<String, BigDecimal> cryptoRates;
        if ("USD".equalsIgnoreCase(targetCurrency)) {
            cryptoRates = cryptoRatesToUsd;
        } else if ("EUR".equalsIgnoreCase(targetCurrency)) {
            cryptoRates = cryptoRatesToEur;
        } else cryptoRates = cryptoRatesToRub;

        String bestCrypto = null;
        BigDecimal bestRate = BigDecimal.ZERO;
        final int SCALE = 4;
        int i = 0;

        for (String crypto : cryptoRates.keySet()) {
            BigDecimal convertedAmount = amount.divide(costList.get(i), SCALE, RoundingMode.HALF_EVEN)
                    .multiply(cryptoRates.get(crypto));

            //  Comparing convertedAmount with the current bestRate
            int compareResult = convertedAmount.compareTo(bestRate);

            //  If convertedAmount is greater than bestRate, update bestRate
            if (compareResult > 0) {
                bestRate = convertedAmount;
                bestCrypto = crypto;
            }
            i++;
        }
        result.setConvertedAmount(bestRate);
        result.setConversionPath(String.format("The best way from %s to %s across %s will be costs %s", sourceCurrency, targetCurrency, bestCrypto, bestRate));
        return result;
    }
}