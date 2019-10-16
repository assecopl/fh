package pl.fhframework.core.rules.builtin;

import pl.fhframework.core.FhException;
import pl.fhframework.core.rules.BusinessRule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalField;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by pawel.ruta on 2017-09-21.
 */
@BusinessRule(categories = "date")
public class DateUtils {
    public LocalDate dateNow(){
        return LocalDate.now();
    }

    public LocalDate dateOf(int year, int month, int dayOfMonth) {
        return LocalDate.of(year, month, dayOfMonth);
    }

    public int dateYear(LocalDate date) {
        return date.getYear();
    }

    public int dateYear(Date time) {
        return ldtFromDate(time).getYear();
    }

    public int dateMonth(LocalDate date) {
        return date.getMonthValue();
    }

    public int dateMonth(Date time) {
        return ldtFromDate(time).getMonthValue();
    }

    public int dateWeek(LocalDate date) {
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return date.get(weekOfYear);
    }

    public int dateWeek(Date time) {
        TemporalField weekOfYear = WeekFields.of(Locale.getDefault()).weekOfWeekBasedYear();
        return ldtFromDate(time).get(weekOfYear);
    }

    public int dateDayOfYear(LocalDate date) {
        return date.getDayOfYear();
    }

    public int dateDayOfYear(Date time) {
        return ldtFromDate(time).getDayOfYear();
    }

    public int dateDayOfMonth(LocalDate date) {
        return date.getDayOfMonth();
    }

    public int dateDayOfMonth(Date time) {
        return ldtFromDate(time).getDayOfMonth();
    }

    public int dateDayOfWeek(LocalDate date) {
        return date.getDayOfWeek().getValue();
    }

    public int dateDayOfWeek(Date time) {
        return ldtFromDate(time).getDayOfWeek().getValue();
    }

    public LocalDate dateAddDays(LocalDate date, int days) {
        return date.plusDays(days);
    }

    public Date dateAddDays(Date time, int days) {
        return dFromLdt(ldtFromDate(time).plusDays(days));
    }

    public LocalDate dateAddWeeks(LocalDate date, int weeks) {
        return date.plusWeeks(weeks);
    }

    public Date dateAddWeeks(Date time, int weeks) {
        return dFromLdt(ldtFromDate(time).plusWeeks(weeks));
    }

    public LocalDate dateAddMonths(LocalDate date, int months) {
        return date.plusMonths(months);
    }

    public Date dateAddMonths(Date time, int months) {
        return dFromLdt(ldtFromDate(time).plusMonths(months));
    }

    public LocalDate dateAddYears(LocalDate date, int years) {
        return date.plusYears(years);
    }

    public Date dateAddYears(Date time, int years) {
        return dFromLdt(ldtFromDate(time).plusYears(years));
    }

    public LocalDate dateMonthStart(LocalDate date) {
        return date.withDayOfMonth(1);
    }

    public Date dateMonthStart(Date time) {
        return dFromLdt(ldtFromDate(time).withDayOfMonth(1));
    }

    public LocalDate dateMonthEnd(LocalDate date) {
        return date.withDayOfMonth(date.lengthOfMonth());
    }

    public Date dateMonthEnd(Date time) {
        return dFromLdt(ldtFromDate(time).withDayOfMonth(ldFromDate(time).lengthOfMonth()));
    }

    public LocalDate dateYearStart(LocalDate date) {
        return date.withDayOfYear(1);
    }

    public Date dateYearStart(Date time) {
        return dFromLdt(ldtFromDate(time).withDayOfYear(1));
    }

    public LocalDate dateYearEnd(LocalDate date) {
        return date.withDayOfYear(date.lengthOfYear());
    }

    public Date dateYearEnd(Date time) {
        return dFromLdt(ldtFromDate(time).withDayOfYear(ldFromDate(time).lengthOfYear()));
    }

    public Date dateAtTime(LocalDate date, int hour, int minute, int seconds) {
        return dFromLdt(date.atTime(hour, minute, seconds));
    }

    public Date dateAtTime(Date time, int hour, int minute, int seconds) {
        return dFromLdt(ldFromDate(time).atTime(hour, minute, seconds));
    }

    public Date timeNow(){
        return Calendar.getInstance().getTime();
    }

    public Date timeOf(int year, int month, int dayOfMonth) {
        return dFromLdt(LocalDateTime.of(year, month, dayOfMonth, 0, 0));
    }

    public Date timeOf(int year, int month, int dayOfMonth, int hour, int minute, int second) {
        return dFromLdt(LocalDateTime.of(year, month, dayOfMonth, hour, minute, second));
    }

    public int timeHour(Date time) {
        return ldtFromDate(time).getHour();
    }

    public int timeMinute(Date time) {
        return ldtFromDate(time).getMinute();
    }

    public int timeSecond(Date time) {
        return ldtFromDate(time).getSecond();
    }

    public Date timeTruncate(Date time) {
        return dFromLdt(ldtFromDate(time).truncatedTo(ChronoUnit.DAYS));
    }

    public Date timeAddHours(Date time, int hours) {
        return dFromLdt(ldtFromDate(time).plusHours(hours));
    }

    public Date timeAddMinutes(Date time, int minutes) {
        return dFromLdt(ldtFromDate(time).plusMinutes(minutes));
    }

    public Date timeAddSeconds(Date time, int seconds) {
        return dFromLdt(ldtFromDate(time).plusSeconds(seconds));
    }

    public int dateDaysBetween(LocalDate date1, LocalDate date2) {
        return (int) ChronoUnit.DAYS.between(date1, date2);
    }

    public int dateDaysBetween(Date date1, Date date2) {
        return dateDaysBetween(ldFromDate(date1), ldFromDate(date2));
    }

    LocalDate dateFrom(Object from) {
        if (from instanceof String) {
            return LocalDate.parse((CharSequence) from);
        }
        else if (from instanceof Date) {
            return ldFromDate((Date) from);
        }
        else if (from instanceof LocalDate) {
            return (LocalDate) from;
        }
        throw new FhException("Unsupported type for conversion");
    }

    Date timeFrom(Object from) {
        if (from instanceof String) {
            return dFromLdt(LocalDateTime.parse((CharSequence) from));
        }
        else if (from instanceof LocalDate) {
            return dFromLdt(LocalDateTime.of((LocalDate) from, LocalTime.now()));
        }
        else if (from instanceof Date) {
            return (Date) from;
        }
        throw new FhException("Unsupported type for conversion");
    }

    public static Date dFromLdt(LocalDateTime ldt) {
        return Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate ldFromDate(Date time) {
        return LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime ldtFromDate(Date time) {
        return LocalDateTime.ofInstant(time.toInstant(), ZoneId.systemDefault());
    }

    public static String getTimestamp() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd_HH_mm_ss"));
    }
}
