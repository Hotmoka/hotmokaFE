package com.hotmokafe.application.utils;

public class StringUtils {

    /**
     * Metodo per controllare se una stringa è vuota
     * @param s
     * @return true se la stringa è vuota (nessun carattere presente) false altrimenti
     */
    public static boolean stringIsNUllOrEmpty(String s){
        return s == null || s.isEmpty();
    }

    /**
     * Metodo per controllare se una stringa non è vuota o nulla
     * @param s
     * @return true se la stringa è valorizzata false altrimenti
     */
    public static boolean isValid(String s){
        return !stringIsNUllOrEmpty(s);
    }
}
