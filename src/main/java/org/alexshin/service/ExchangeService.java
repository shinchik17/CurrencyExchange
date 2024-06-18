package org.alexshin.service;

import org.alexshin.model.Currency;
import org.alexshin.model.ExchangeRate;
import org.alexshin.model.response.ExchangeResponse;
import org.alexshin.repository.IRepository;
import org.alexshin.repository.JDBCCurrencyRepository;
import org.alexshin.repository.JDBCExchangeRatesRepository;

import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    private static final JDBCCurrencyRepository currencyRepository = new JDBCCurrencyRepository();
    private static final JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();


    // TODO:
    public ExchangeResponse getExchangeResponse(Currency baseCurrency, Currency targetCurrency, double amount) throws SQLException {

        ExchangeRate exchangeRate = getExchangeRate(baseCurrency, targetCurrency).orElseThrow();
        double convertedAmount = amount * exchangeRate.getRate();

        return new ExchangeResponse(baseCurrency,
                targetCurrency,
                exchangeRate.getRate(),
                amount,
                convertedAmount);
    }

    public Optional<ExchangeRate> getExchangeRate(Currency baseCurrency, Currency targetCurrency) throws SQLException{
        var exchangeRate = getFromDirectExchange(baseCurrency, targetCurrency);
        if (exchangeRate.isPresent()){
            return exchangeRate;
        }

        var inverseExchangeRate = getFromInverseExchange(baseCurrency, targetCurrency);
        if (inverseExchangeRate.isPresent()){
            return inverseExchangeRate;
        }

        // IDEA предложила красивое упрощение вместо отдельной ветки if :)
        return getFromCrossExchange(baseCurrency, targetCurrency);
    }


    public Optional<ExchangeRate> getFromCrossExchange(Currency baseCurrency, Currency targetCurrency) throws SQLException {
        Optional<Double> baseToUsdRate = getCurrencyToUsdRate(baseCurrency);
        Optional<Double> targetToUsdRate = getCurrencyToUsdRate(targetCurrency);

        if (baseToUsdRate.isEmpty() || targetToUsdRate.isEmpty()){
            return Optional.empty();
        }

        return Optional.of(new ExchangeRate(baseCurrency.getId(),
                                            targetCurrency.getId(),
                                        baseToUsdRate.get() / targetToUsdRate.get()));
    }


    public Optional<Double> getCurrencyToUsdRate(Currency currency) throws SQLException {

        Optional<ExchangeRate> usdToBaseCurrencyRate = exchangeRatesRepository.findByUsdBase(currency.getCode());

        double toUsdRate;
        if (usdToBaseCurrencyRate.isEmpty()) {
            Optional<ExchangeRate> baseCurrencyToUsdRate = exchangeRatesRepository.findByUsdTarget(currency.getCode());
            if (baseCurrencyToUsdRate.isEmpty()){
                return Optional.empty();
            }
            toUsdRate = baseCurrencyToUsdRate.get().getRate();

        } else {
            toUsdRate = 1 / usdToBaseCurrencyRate.get().getRate();
        }

        return Optional.of(toUsdRate);

    }


    public Optional<ExchangeRate> getFromInverseExchange(Currency baseCurrency, Currency targetCurrency) throws SQLException {

        Optional<ExchangeRate> optionalInverseExchangeRate = exchangeRatesRepository.findByCodes(targetCurrency.getCode(),
                baseCurrency.getCode());

        if (optionalInverseExchangeRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate inverseExchangeRate = optionalInverseExchangeRate.get();

        return Optional.of(new ExchangeRate(inverseExchangeRate.getTargetCurrencyId(),
                                            inverseExchangeRate.getBaseCurrencyId(),
                                        1 / inverseExchangeRate.getRate()));
    }


    public Optional<ExchangeRate> getFromDirectExchange(Currency baseCurrency, Currency targetCurrency) throws SQLException {

        return exchangeRatesRepository.findByCodes(baseCurrency.getCode(), targetCurrency.getCode());
    }

}
