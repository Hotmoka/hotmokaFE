package com.hotmokafe.application.utils;

import java.util.List;

public class ListUtils {
    /**
     * Check if a list exists or it doesn't contain at least one element
     * @param list a list to check
     * @return true if the param is null or doesn't contain any elements, false otherwise
     */
    public static boolean isEmpty(List list){
        return list == null || list.isEmpty();
    }

    /**
     * Check if a list exists and it contains at least one element
     * @param list a list to check
     * @return true if the param exists and contains at least one elements, false otherwise
     */
    public static boolean isValid(List list){
        return !isEmpty(list);
    }
}
