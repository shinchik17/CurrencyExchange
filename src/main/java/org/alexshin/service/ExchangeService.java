package org.alexshin.service;

import org.alexshin.model.ExchangeResponse;
import org.alexshin.model.entity.ExchangeRate;
import org.alexshin.model.mapper.ExchangeRateMapper;
import org.alexshin.repository.JDBCExchangeRatesRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ExchangeService {
    private static final JDBCExchangeRatesRepository exchangeRatesRepository = new JDBCExchangeRatesRepository();
    private final ExchangeRateMapper mapper = ExchangeRateMapper.INSTANCE;

    public ExchangeResponse getExchangeResponse(String baseCode, String targetCode, BigDecimal amount) throws SQLException, NoSuchElementException {

        ExchangeRate exchangeRate = getExchangeRate(baseCode, targetCode).orElseThrow();

        return mapper.exchangeRateToResponse(exchangeRate, amount);

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

        BigDecimal rate = usdToTargetER.get().getRate().divide(usdToBaseER.get().getRate(), 6, RoundingMode.HALF_UP);

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
                (new BigDecimal(1)).divide(inverseExchangeRate.getRate(), 6, RoundingMode.HALF_UP)
                )
        );
    }


    public Optional<ExchangeRate> getFromDirectExchange(String baseCode, String targetCode) throws SQLException {
        return exchangeRatesRepository.findByCodes(baseCode, targetCode);
    }

}
