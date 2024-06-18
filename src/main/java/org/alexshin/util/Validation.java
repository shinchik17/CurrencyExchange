package org.alexshin.util;

public class Validation {


    public static boolean isValidCurrencyCode(String code){
        return code.matches("[a-zA-z]{3}");
    }
}
