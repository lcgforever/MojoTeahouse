package com.mojoteahouse.mojotea.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {

    private static SimpleDateFormat utcDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());

    public static String dateToString(Date date) {
        return utcDateFormat.format(date);
    }

    public static Date stringToDate(String utcDateString) {
        try {
            return utcDateFormat.parse(utcDateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }
}
