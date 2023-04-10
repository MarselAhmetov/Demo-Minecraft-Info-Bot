package ru.demo_bot_minecraft.util;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DateUtils {

    public final static ZoneId TIME_ZONE = ZoneId.of("Europe/Moscow");

    public static LocalDateTime now() {
        return LocalDateTime.now(TIME_ZONE);
    }

    public static LocalDate today() {
        return LocalDate.now(TIME_ZONE);
    }
}
