package com.hotmokafe.application.utils;

public class StringUtils {

    /**
     * Check if a string exists or it doesn't contain at least one character
     * @param s a String to check
     * @return true if the param is null or doesn't contain any elements, false otherwise
     */
    public static boolean stringIsNUllOrEmpty(String s){
        return s == null || s.isEmpty();
    }

    /**
     * Metodo per controllare se una stringa non è vuota o nulla
     * @param s a String to check
     * @return true se la stringa è valorizzata false altrimenti
     */
    public static boolean isValid(String s){
        return !stringIsNUllOrEmpty(s);
    }
}
