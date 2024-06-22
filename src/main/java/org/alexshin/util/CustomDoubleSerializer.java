package org.alexshin.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class CustomDoubleSerializer extends JsonSerializer<Double> {
    private int precision = 6;

    public CustomDoubleSerializer (int precision) {
        this.precision = precision;
    }

    public CustomDoubleSerializer () {
    }

    @Override
    public void serialize(Double value, JsonGenerator gen, SerializerProvider provider) throws IOException {

        if (precision == 0) {
            gen.writeNumber(value);
        } else {
            BigDecimal bd = new BigDecimal(value);
            bd = bd.setScale(precision, RoundingMode.HALF_UP);
            gen.writeNumber(bd.doubleValue());
        }

    }
}
