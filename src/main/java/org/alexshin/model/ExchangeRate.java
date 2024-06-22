package org.alexshin.model;


import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.alexshin.util.CustomDoubleSerializer;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRate {
    private int id;
    private Currency baseCurrency;
    private Currency targetCurrency;
    @JsonSerialize(using = CustomDoubleSerializer.class)
    private double rate;


    public ExchangeRate(Currency baseCurrency, Currency targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }
}
