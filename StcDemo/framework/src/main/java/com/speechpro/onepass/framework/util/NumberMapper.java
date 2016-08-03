package com.speechpro.onepass.framework.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @author volobuev
 * @since 22.04.16
 */
public final class NumberMapper {

    private static final Map<String, Integer> numbers = new HashMap<>(10);

    static {
        numbers.put("zero", 0);
        numbers.put("one", 1);
        numbers.put("two", 2);
        numbers.put("three", 3);
        numbers.put("four", 4);
        numbers.put("five", 5);
        numbers.put("six", 6);
        numbers.put("seven", 7);
        numbers.put("eight", 8);
        numbers.put("nine", 9);
        numbers.put("um", 1);
        numbers.put("dois", 2);
        numbers.put("três", 3);
        numbers.put("quatro", 4);
        numbers.put("cinco", 5);
        numbers.put("seis", 6);
        numbers.put("sete", 7);
        numbers.put("oito", 8);
        numbers.put("nove", 9);
    }

    public static String convert(String in) {
        String[] nums = in.split("\\s+");
        StringBuilder sb = new StringBuilder("");
        for (String num : nums) {
            sb.append(numbers.get(num));
        }
        return sb.toString();
    }

}
