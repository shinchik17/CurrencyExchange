package org.alexshin.model.mapper;


import org.alexshin.model.ExchangeResponse;
import org.alexshin.model.entity.ExchangeRate;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Mapper
public interface ExchangeRateMapper {


    ExchangeRateMapper INSTANCE = Mappers.getMapper(ExchangeRateMapper.class);

    @Mapping(source = "amount", target = "amount")
    @Mapping(target = "convertedAmount", expression = "java(getConvertedAmount(exchangeRate, amount))")
    ExchangeResponse exchangeRateToResponse(ExchangeRate exchangeRate, BigDecimal amount);

    default BigDecimal getConvertedAmount(ExchangeRate exchangeRate, BigDecimal amount) {
        return amount.multiply(exchangeRate.getRate()).setScale(2, RoundingMode.HALF_UP);
    }


}
