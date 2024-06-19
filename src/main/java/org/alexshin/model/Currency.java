package org.alexshin.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "code", "sign"})
public class Currency {
    private int id;
    private String code;

    @JsonProperty(value = "name")
    private String fullName;

    private String sign;


    public Currency(String code, String fullName, String sign) {
        this.code = code;
        this.fullName = fullName;
        this.sign = sign;
    }
}
