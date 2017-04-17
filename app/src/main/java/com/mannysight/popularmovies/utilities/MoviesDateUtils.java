package com.mannysight.popularmovies.utilities;

import android.content.Context;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by wamuo on 4/17/2017.
 */

public class MoviesDateUtils {


    public static long getLocalDateFromUTC(long utcDate) {
        TimeZone tz = TimeZone.getDefault();
        long gmtOffset = tz.getOffset(utcDate);
        return utcDate - gmtOffset;
    }


    public static long getDateLongFromString(String stringDate) {
        SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd");
        long milliseconds = 0;
        try {
            Date d = f.parse(stringDate);
            milliseconds = d.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return milliseconds;
    }

    public static String getFriendlyDateString(Context context, long dateInMillis) {

        long localDate = getLocalDateFromUTC(dateInMillis);

        int flags = android.text.format.DateUtils.FORMAT_SHOW_DATE
                | android.text.format.DateUtils.FORMAT_SHOW_YEAR
                | android.text.format.DateUtils.FORMAT_ABBREV_ALL;

        return android.text.format.DateUtils.formatDateTime(context, localDate, flags);

    }

}
