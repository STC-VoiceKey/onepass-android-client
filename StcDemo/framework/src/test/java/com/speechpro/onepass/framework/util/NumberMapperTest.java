package com.speechpro.onepass.framework.util;

import android.content.Context;
import android.os.Build;

import com.speechpro.onepass.framework.BuildConfig;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by alexander on 14.09.17.
 */

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.P, packageName = "com.speechpro.onepass.framework")
public class NumberMapperTest {

    private Context context;

    @Before
    public void setUp() throws Exception {
        context = RuntimeEnvironment.application;
    }

    @Test
    @Config(qualifiers="ru")
    public void test_ru_convert() throws Exception {
        NumberMapper.clearMap();
        assertEquals("123578", NumberMapper.convert(context, "один два три пять семь восемь"));
        assertEquals("952014", NumberMapper.convert(context, "девять пять два ноль один четыре"));
        assertEquals("439071", NumberMapper.convert(context, "четыре три девять ноль семь один"));
        assertNotEquals("952014", NumberMapper.convert(context, "девять пять два нуль один четыре"));
        assertNotEquals("439071", NumberMapper.convert(context, "четыре три девять нуль семь один"));
        assertNotEquals("123578", NumberMapper.convert(context, "четыре два три пять семь восемь"));
    }

    @Test
    @Config(qualifiers="en")
    public void test_en_convert() throws Exception {
        NumberMapper.clearMap();
        assertEquals("123578", NumberMapper.convert(context, "one two three five seven eight"));
        assertEquals("952014", NumberMapper.convert(context, "nine five two zero one four"));
        assertEquals("439071", NumberMapper.convert(context, "four three nine zero seven one"));
        assertNotEquals("952014", NumberMapper.convert(context, "nine four two zero six four"));
        assertNotEquals("439071", NumberMapper.convert(context, "four three nine two seven one"));
        assertNotEquals("123578", NumberMapper.convert(context, "four two three five seven eight"));
    }

}