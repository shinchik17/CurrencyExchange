package org.alexshin.DTO;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexshin.model.Currency;
import org.alexshin.util.CustomDoubleSerializer;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeResponse {
    private Currency baseCurrency;
    private Currency targetCurrency;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private double exchangeRate;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private double amount;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private double convertedAmount;
}

