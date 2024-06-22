package org.alexshin.util;

public class Validation {


    public static boolean isValidCurrencyCode(String code){
        return isValidString(code) && code.matches("[a-zA-z]{3}");
    }

    public static boolean isValidString (String s){
        return s != null && !s.isBlank();
    }


    public static boolean isValidRate(String rate) {
        return isValidString(rate) && rate.matches("^[0-9]+\\.*[0-9]*$");
    }

    public static boolean isValidExchangeRateString(String rateString){
        return isValidString(rateString) && rateString.matches("[a-zA-Z]{6}");
    }


}
