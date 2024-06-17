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
    public ExchangeResponse getExchangeResponse(Currency baseCurrency, Currency targetCurrency, double amount){
        return null;
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
        // TODO: вынести в отдельный метод поиск по USD в двух направлениях
        Optional<ExchangeRate> usdToBaseCurrencyRate = exchangeRatesRepository.findByUsdBase(baseCurrency.getCode());
        double baseToUsdRate;
        if (usdToBaseCurrencyRate.isEmpty()) {
            Optional<ExchangeRate> baseCurrencyToUsdRate = exchangeRatesRepository.findByUsdTarget(baseCurrency.getCode());
            if (baseCurrencyToUsdRate.isEmpty()){
                return Optional.empty();
            }
            baseToUsdRate = baseCurrencyToUsdRate.get().getRate();

        } else {
            baseToUsdRate = 1 / usdToBaseCurrencyRate.get().getRate();
        }


        Optional<ExchangeRate> usdToTargetCurrencyRate = exchangeRatesRepository.findByUsdBase(targetCurrency.getCode());
        double targetToUsdRate;
        if (usdToTargetCurrencyRate.isEmpty()) {
            Optional<ExchangeRate> targetCurrencyToUsdRate = exchangeRatesRepository.findByUsdTarget(targetCurrency.getCode());
            if (targetCurrencyToUsdRate.isEmpty()){
                return Optional.empty();
            }
            targetToUsdRate = targetCurrencyToUsdRate.get().getRate();

        } else {
            targetToUsdRate = 1 / usdToTargetCurrencyRate.get().getRate();
        }


        return Optional.of(new ExchangeRate(baseCurrency.getId(),
                                            targetCurrency.getId(),
                                        baseToUsdRate/targetToUsdRate));
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
