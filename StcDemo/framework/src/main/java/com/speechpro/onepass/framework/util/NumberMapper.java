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

    private static final Map<String, Integer> numbers = new HashMap<>();

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

    public static void clearMap() {
        numbers.clear();
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
