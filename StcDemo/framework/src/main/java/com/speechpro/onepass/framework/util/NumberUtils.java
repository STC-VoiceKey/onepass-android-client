package com.speechpro.onepass.framework.util;

import android.content.Context;

import com.speechpro.onepass.framework.R;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by grigal on 23.06.2017.
 */

public class NumberUtils {

    /** Random number generator
     *
     */
    private static Random RANDOM = new SecureRandom();

    /**
     * Creates a string of random decimal.
     *
     * @param nValues the amount of characters to generate
     *
     * @return an string containing <code>nValues</code> decimal
     */
    public static String createRandomValues(int nValues) {
        List<Integer> digits = new ArrayList<>();
        for (int i = 0; i < nValues; i++) {
            digits.add(i);
        }

        StringBuffer buffer = new StringBuffer();

        for (int i = 0; i < nValues; i++) {
            int random = RANDOM.nextInt(digits.size());
            buffer.append(digits.get(random));
            digits.remove(random);
        }

        return String.valueOf(buffer);
    }

    /**
     * Returns a psuedo-random number between min and max, inclusive.
     * The difference between min and max can be at most
     * <code>Integer.MAX_VALUE - 1</code>.
     *
     * @param min Minimim value
     * @param max Maximim value.  Must be greater than min.
     * @return Integer between min and max, inclusive.
     * @see java.util.Random#nextInt(int)
     */
    public static int randInt(int min, int max) {

        // Usually this can be a field rather than a method variable
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public static String convertPhraseDecimalToString(Context ctx, String phrase) {
        char[] ret = phrase.toCharArray();
        String[] numbers = ctx.getResources().getStringArray(R.array.numbers);
        StringBuffer buffer = new StringBuffer("");
        for (int i = 0; i < ret.length; i++) {
            buffer.append(numbers[(Character.getNumericValue(ret[i]))]);
            buffer.append(" ");
        }
        return buffer.toString().trim();
    }

}
