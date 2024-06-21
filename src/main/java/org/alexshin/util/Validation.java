package org.alexshin.util;

public class Validation {


    public static boolean isValidCurrencyCode(String code){
        return isValidString(code) && code.matches("[a-zA-z]{3}");
    }

    public static boolean isValidString (String s){
        return s != null && !s.isBlank();
    }


    public static boolean isValidRate(String rate) {

        // TODO:  скорее всего избыточная проверка parseDouble, чекнуть потом
        if (isValidString(rate) && rate.matches("^[0-9]+\\.*[0-9]*$")) {
            try {
                Double.parseDouble(rate);
                return true;
            } catch (NumberFormatException e){
                return false;
            }
        }

        return false;
    }

    public static boolean isValidExchangeRateString(String rateString){
        return rateString.matches("[a-zA-Z]{6}");
    }


}
