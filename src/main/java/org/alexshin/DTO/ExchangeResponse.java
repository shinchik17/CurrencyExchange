package org.alexshin.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexshin.model.Currency;

import java.math.BigDecimal;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponse {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private BigDecimal rate;
    private BigDecimal amount;
    private BigDecimal convertedAmount;
}

