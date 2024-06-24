package org.alexshin.service;

import org.alexshin.DTO.ExchangeResponse;
import org.alexshin.model.ExchangeRate;
import org.alexshin.repository.JDBCExchangeRatesRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeService {
    private static final JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();


    public ExchangeResponse getExchangeResponse(String baseCode, String targetCode, BigDecimal amount) throws SQLException, NoSuchElementException {

        ExchangeRate exchangeRate = getExchangeRate(baseCode, targetCode).orElseThrow();
        BigDecimal convertedAmount = amount.multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_UP);

        return new ExchangeResponse(exchangeRate.getBaseCurrency(),
                exchangeRate.getBaseCurrency(),
                exchangeRate.getRate(),
                amount,
                convertedAmount);
    }

    public Optional<ExchangeRate> getExchangeRate(String baseCode, String targetCode) throws SQLException {
        var exchangeRate = getFromDirectExchange(baseCode, targetCode);
        if (exchangeRate.isPresent()) {
            return exchangeRate;
        }

        var inverseExchangeRate = getFromInverseExchange(baseCode, targetCode);
        if (inverseExchangeRate.isPresent()) {
            return inverseExchangeRate;
        }

        return getFromCrossExchange(baseCode, targetCode);
    }

    public Optional<ExchangeRate> getFromCrossExchange(String baseCode, String targetCode) throws SQLException {

        List<ExchangeRate> erList = exchangeRatesRepository.findByCodesWithUsdBase(baseCode,
                targetCode);

        Optional<ExchangeRate> usdToBaseER = erList.stream()
                .filter(er -> er.getTargetCurrency().getCode().equals(baseCode))
                .findFirst();

        if (usdToBaseER.isEmpty()) {
            return Optional.empty();
        }

        Optional<ExchangeRate> usdToTargetER = erList.stream()
                .filter(er -> er.getTargetCurrency().getCode().equals(targetCode))
                .findFirst();

        if (usdToTargetER.isEmpty()) {
            return Optional.empty();
        }

        BigDecimal rate = usdToBaseER.get().getRate().divide(usdToTargetER.get().getRate(), RoundingMode.HALF_UP);

        return Optional.of(new ExchangeRate(
                usdToBaseER.get().getBaseCurrency(),
                usdToTargetER.get().getTargetCurrency(),
                rate)
        );

    }


    public Optional<ExchangeRate> getFromInverseExchange(String baseCode, String targetCode) throws SQLException {

        Optional<ExchangeRate> optionalInverseExchangeRate = exchangeRatesRepository.findByCodes(targetCode,
                baseCode);

        if (optionalInverseExchangeRate.isEmpty()) {
            return Optional.empty();
        }

        ExchangeRate inverseExchangeRate = optionalInverseExchangeRate.get();

        return Optional.of(new ExchangeRate(
                inverseExchangeRate.getTargetCurrency(),
                inverseExchangeRate.getBaseCurrency(),
                (new BigDecimal(1)).divide(inverseExchangeRate.getRate(), RoundingMode.HALF_UP)
                )
        );
    }


    public Optional<ExchangeRate> getFromDirectExchange(String baseCode, String targetCode) throws SQLException {
        return exchangeRatesRepository.findByCodes(baseCode, targetCode);
    }

}
