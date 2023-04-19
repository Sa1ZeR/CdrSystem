package me.sa1zer.cdrsystem.common.utils;

import lombok.experimental.UtilityClass;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@UtilityClass
public class TimeUtils {

    private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private static final SimpleDateFormat sdfs = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static LocalDateTime parseTimeFromString(String s) throws ParseException {
        return sdf.parse(s).toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }

    public static String LocalDateTimeToString(LocalDateTime time) {
        return sdfs.format(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static String LocalDateTimeToCdr(LocalDateTime time) {
        return sdf.format(Date.from(time.atZone(ZoneId.systemDefault()).toInstant()));
    }

    public static String getDurationString(long duration) {
        long diffSeconds = duration % 60;
        long diffMinutes = duration / 60 % 60;
        long diffHours = duration / (60 * 60) % 24;

        return String.format("%s:%s:%s",
                diffHours < 10 ? "0" + diffHours : diffHours,
                diffMinutes < 10 ? "0" + diffMinutes : diffMinutes,
                diffSeconds < 10 ? "0" + diffSeconds : diffSeconds);
    }
}
