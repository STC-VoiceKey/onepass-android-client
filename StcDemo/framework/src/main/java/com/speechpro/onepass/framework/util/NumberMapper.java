package com.speechpro.onepass.framework.util;

import android.content.Context;
import android.support.annotation.NonNull;

import com.speechpro.onepass.framework.R;

import java.util.HashMap;
import java.util.Map;

/**
 * @author volobuev
 * @since 22.04.16
 */
public final class NumberMapper {

    private static final String TAG = NumberMapper.class.getSimpleName();

    private static final Map<String, Integer> numbers = new HashMap<>(11, 1);

//    static {
//        numbers.put("zero", 0);
//        numbers.put("one", 1);
//        numbers.put("two", 2);
//        numbers.put("three", 3);
//        numbers.put("four", 4);
//        numbers.put("five", 5);
//        numbers.put("six", 6);
//        numbers.put("seven", 7);
//        numbers.put("eight", 8);
//        numbers.put("nine", 9);
//        numbers.put("ноль", 0);
//        numbers.put("нуль", 0);
//        numbers.put("один", 1);
//        numbers.put("два", 2);
//        numbers.put("три", 3);
//        numbers.put("четыре", 4);
//        numbers.put("пять", 5);
//        numbers.put("шесть", 6);
//        numbers.put("семь", 7);
//        numbers.put("восемь", 8);
//        numbers.put("девять", 9);
//        numbers.put("um", 1);
//        numbers.put("dois", 2);
//        numbers.put("três", 3);
//        numbers.put("quatro", 4);
//        numbers.put("cinco", 5);
//        numbers.put("seis", 6);
//        numbers.put("sete", 7);
//        numbers.put("oito", 8);
//        numbers.put("nove", 9);
//    }

    @NonNull
    public static String convert(Context ctx, String in) {
        if (numbers.isEmpty()) {
            initMap(ctx);
        }
        String[] nums = in.split("\\s+");
        StringBuilder sb = new StringBuilder("");
        for (String num : nums) {
            Integer i = numbers.get(num);
            if (i != null) {
                sb.append(numbers.get(num));
            } else {
                continue;
            }
        }

        return sb.toString();
    }

    private static void initMap(Context ctx) {
        String[] stringArray = ctx.getResources().getStringArray(R.array.numbers);
        int value = 0;
        for (String key : stringArray) {
            numbers.put(key, value);
            value++;
        }
    }

}
