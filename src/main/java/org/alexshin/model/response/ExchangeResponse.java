package org.alexshin.model.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexshin.model.Currency;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponse {
    private Currency baseCurrency;
    private Currency targetCurrency;
    private double exchangeRate;
    private double amount;
    private double convertedAmount;
}

