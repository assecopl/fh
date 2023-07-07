/*
 * DateHelper.java
 *
 * Prawa autorskie do oprogramowania i jego kodów źródłowych
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 *
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.prints.printservice;



import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.FormatStyle;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * @author Dariusz Skrudlik
 */
public class DateHelper {

    private static final Logger logger = LoggerFactory.getLogger(DateHelper.class);

    /**
     * Funkcja  parsuje datę wg zadanego formatu.
     */
    public static java.util.Date stringToDate(String strDate, String format) {
        return stringToDate(strDate, format, null);
    }

    public static java.util.Date stringToDate(String strDate, String format, TimeZone timeZone) {
        if (strDate == null || format == null) {
            throw new IllegalArgumentException("Nieprawidłowy argument, przekazano wartość 'null'!");
        }

        if (strDate.indexOf("T") != -1) {
            strDate = strDate.replaceAll("T", " ");
        }
        if (format.indexOf("T") != -1) {
            format = format.replaceAll("T", " ");
        }

        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            if (timeZone != null) {
                simpleDateFormat.setTimeZone(timeZone);
            }
            return simpleDateFormat.parse(strDate);
        } catch (ParseException pe) {
            throw new RuntimeException("Nieprawidłowy format daty '" + format + "' dla " + strDate);
        }
    }

    /**
     * Parse string with format 'yyyy-MM-dd?HH:mm:ss' where ? can be 'T' or ' '
     *
     * @param strDate
     * @return
     */
    public static Date stringToDateTime(String strDate) {
        return stringToDate(strDate, "yyyy-MM-dd HH:mm:ss");
    }

    public static Date stringXMLUTCToDateTime(String strDate) {
        return stringToDate(strDate, "yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("UTC"));
    }

    /**
     * Funkcja parsuje przekazany string do daty - przyjmując domyślny format "yyyy-MM-dd"
     */
    public static java.util.Date stringToDate(String strDate) {
        java.util.Date d = stringToDate(strDate, "yyyy-MM-dd");
        return d;

    }

    public static String localDateToString(LocalDate date, String format) {
        if (date == null || format == null) {
            return null;
        }
        boolean forXml = format.contains("T");
        SimpleDateFormat simpleDateFormat;

        if (format.equals("yyyy-MM-dd")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else if (format.equals("yyyy-MM-dd")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        } else {
            simpleDateFormat = new SimpleDateFormat(format);
        }

        String result = simpleDateFormat.format(date);
        if (forXml) {
            result = result.replaceAll(" ", "T");
        }
        return result;
    }

    public static String localDateTimeToString(LocalDateTime date, String format) {
            return localDateTimeToString(date, format, null);
        }

    public static String localDateTimeToString(LocalDateTime date, String format, ZoneId timeZone) {
        if (date == null || format == null) {
            return null;
        }
        boolean forXml = format.contains("T");
        DateTimeFormatter  dateTimeFormatter;

        if (format.equals("yyyy-MM-ddTHH:mm:ss")) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        } else if (format.equals("yyyy-MM-ddThh:mm:ss")) {
            dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        } else {
            dateTimeFormatter = DateTimeFormatter.ofPattern(format);
        }
        if (timeZone != null) {
            dateTimeFormatter.withZone(timeZone);
        }

        String result = dateTimeFormatter.format(date);
        if (forXml) {
            result = result.replaceAll(" ", "T");
        }
        return result;
    }

    public static String dateToString(java.util.Date date, String format) {
        return dateToString(date, format, null);
    }

    /**
     * Funkcja konwertuje date do tekstu
     *
     * @param date
     * @param format
     * @param timeZone
     * @return
     */
    public static String dateToString(java.util.Date date, String format, TimeZone timeZone) {
        if (date == null || format == null) {
            return null;
        }
        boolean forXml = format.contains("T");
        SimpleDateFormat simpleDateFormat;

        if (format.equals("yyyy-MM-ddTHH:mm:ss")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else if (format.equals("yyyy-MM-ddThh:mm:ss")) {
            simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        } else {
            simpleDateFormat = new SimpleDateFormat(format);
        }
        if (timeZone != null) {
            simpleDateFormat.setTimeZone(timeZone);
        }

        String result = simpleDateFormat.format(date);
        if (forXml) {
            result = result.replaceAll(" ", "T");
        }
        return result;
    }

    public static String dateToString(java.util.Date date) {
        return dateToString(date, "yyyy-MM-dd");
    }

    public static String dateTimeToString(java.util.Date date) {
        return dateToString(date, "yyyy-MM-dd HH:mm:ss");
    }

    public static String dateTimeToXMLString(java.util.Date date) {
        return dateToString(date, "yyyy-MM-ddTHH:mm:ss");
    }

    public static String dateTimeToXMLUTCString(java.util.Date date) {
        return dateToString(date, "yyyy-MM-ddTHH:mm:ss", TimeZone.getTimeZone("UTC"));
    }

    /**
     * Zwraca dzien tygodnia w naszym standardzie
     */
    public static final int decodeDayOfWeek(int dayNumber) {

        switch (dayNumber) {
            case 1:
                return 7;
            case 2:
                return 1;
            case 3:
                return 2;
            case 4:
                return 3;
            case 5:
                return 4;
            case 6:
                return 5;
            case 7:
                return 6;
            default:
                return dayNumber;
        }
    }

    /**
     * Converts XMLGregorianCalendar to java.util.Date. Time zone aware.
     *
     * @param inCalendar - XMLGregorianCalendar date.
     * @return Date.
     */
    public static final Date XMLGregorianCalendar2Date(XMLGregorianCalendar inCalendar) {
        // Do not try to convert null value
        if (inCalendar == null) {
            return null;
        }
        // Otherwise converting.
        GregorianCalendar gregCal = inCalendar.toGregorianCalendar();
        return gregCal.getTime();
    }

    public static XMLGregorianCalendar LocalDate2XmlGregorianCalendar(LocalDate date) {
        if(date != null) {
            return LocalDateTime2XmlGregorianCalendar(date.atStartOfDay());
        } else {
            return null;
        }

    }

    public static XMLGregorianCalendar LocalDateTime2XmlGregorianCalendar(LocalDateTime date) {
        if(date == null) return null;
        GregorianCalendar gregCal = GregorianCalendar.from(date.atZone(ZoneId.systemDefault()));
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (DatatypeConfigurationException ex) {
            logger.warn("Problem with Date2XMLGregorianCalendar.", ex);
            throw new RuntimeException("DatatypeConfigurationException", ex);
        }
    }


    public static final XMLGregorianCalendar Date2XMLGregorianCalendar(Date in) {
        return Date2XMLGregorianCalendar(in, TimeZone.getDefault());
    }

    public static final XMLGregorianCalendar Date2XMLGregorianCalendar(Date inDate, TimeZone outTimeZone) {
        // Do not try to convert null value
        if (inDate == null) {
            return null;
        }
        if (outTimeZone == null) {
            outTimeZone = TimeZone.getDefault();
        }
        // Otherwise converting.
        GregorianCalendar gregCal = (GregorianCalendar) GregorianCalendar.getInstance(outTimeZone);
        gregCal.setTime(inDate);
        try {
            return DatatypeFactory.newInstance().newXMLGregorianCalendar(gregCal);
        } catch (DatatypeConfigurationException ex) {
            logger.warn("Problem with Date2XMLGregorianCalendar.", ex);
            throw new RuntimeException("DatatypeConfigurationException", ex);
        }
    }

    /**
     * Get proper TimeZone for passed offset.
     *
     * @param offset - the offset value from -12 to 12
     * @return TIMEZone
     */
    public static TimeZone getTimeZone(int offset) {
        String gmtOffset = null;

        if (-12 <= offset && offset < 0) {
            gmtOffset = "GMT" + Integer.toString(offset) + ":00";
        } else if (0 <= offset && offset <= 12) {
            gmtOffset = "GMT+" + Integer.toString(offset) + ":00";
        } else {
            throw new RuntimeException("Invalid offset for time zone: " + offset);
        }

        return TimeZone.getTimeZone(gmtOffset);

    }

    /**
     * Finds the number of days between the given dates.
     *
     * @param startDate
     * @param endDate
     * @return Number if days
     */
    public static Integer getDateDifference(Date startDate, Date endDate) {
        Integer numberOfDays = Integer.valueOf("0");
        if (startDate != null && endDate != null) {
            numberOfDays = Integer.valueOf(String.valueOf((endDate.getTime() - startDate.getTime()) / (24 * 60 * 60 * 1000)));
        }
        return numberOfDays;
    }

    /**
     * Zwraca datę będącą początkiem dnia (YYYY-MM-DD 00:00:00.000)
     *
     * @param date
     * @return
     */
    public static Date getStartOfDay(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTime();
    }

    /**
     * Zwraca datę będącą końcem dnia (YYYY-MM-DD 23:59:59.999)
     *
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        if (date == null) return null;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);

        return calendar.getTime();
    }

    /**
     * Adds or subtracts the specified amount of time to the given calendar field,
     * based on the calendar's rules.
     *
     * @param date   the start date for operation
     * @param field  the calendar field.
     * @param amount the amount of date or time to be added to the field.
     */
    public static Date add(Date date, int field, int amount) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(field, amount);
            date = calendar.getTime();
        }
        return date;
    }

    /**
     * Ustawia kalendarz na pierwszy dzień w podanym miesiącu {date}.
     *
     * @param date the start date for operation
     */
    public static Calendar firstDayOfMonth(Date date) {
        if (date != null) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            return calendar;
        }
        return null;
    }

    /**
     * Ustawia kalendarz na ostatni dzień w podanym miesiącu {date}.
     *
     * @param date the start date for operation
     */
    public static Calendar lastDayOfMonth(Date date) {
        if (date != null) {
            Calendar calendar = firstDayOfMonth(date);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1);
            return calendar;
        }
        return null;
    }


    /**
     * Szuka ostatniego dnia o zadanym numerze {dayOfWeek} w podanym miesiącu {date}.
     * Dodaje/odejmuje zadaną ilość dni {amountOfDays} do znależionej daty. (mozna znaleźć np. przedostatnią środę poprzez podanie -7)
     *
     * @param date         the start date for operation
     * @param dayOfWeek    the calendar DAY_OF_WEEK.
     * @param amountOfDays the amountOfDays of date or time to be added to the field.
     */
    public static Calendar lastDayOfMonth(Date date, int dayOfWeek, int amountOfDays) {
        if (date != null) {
            Calendar calendar = firstDayOfMonth(date);
            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.DAY_OF_YEAR, -1); // ostatni dzień bieżącego miesiąca
            while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
                calendar.add(Calendar.DAY_OF_YEAR, -1);
            }
            calendar.add(Calendar.DAY_OF_YEAR, amountOfDays);
            return calendar;
        }
        return null;
    }

    /**
     * Szuka pierwszego dnia o zadanym numerze {dayOfWeek} w podanym miesiącu {date}.
     * Dodaje/odejmuje zadaną ilość dni {amountOfDays} do znależionej daty. (mozna znaleźć np. druga środę poprzez podanie +7)
     *
     * @param date         the start date for operation
     * @param dayOfWeek    the calendar DAY_OF_WEEK.
     * @param amountOfDays the amountOfDays of date or time to be added to the field.
     */
    public static Calendar firstDayOfMonth(Date date, int dayOfWeek, int amountOfDays) {
        if (date != null) {
            Calendar calendar = firstDayOfMonth(date);
            while (calendar.get(Calendar.DAY_OF_WEEK) != dayOfWeek) {
                calendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            calendar.add(Calendar.DAY_OF_YEAR, amountOfDays);
            return calendar;
        }
        return null;
    }

    public static Date toDate(LocalDate ld) {
        if (ld != null) {
            return toDate(ld.atStartOfDay());
        }
        return null;
    }

    public static Date toDate(LocalDateTime ldt) {
        if (ldt != null) {
            Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
            return Date.from(instant);
        }
        return null;
    }

    public static LocalDate toLocalDate(Date date) {
        if (date != null) {
            // java.sql.Date has own implementation 
            if (date instanceof java.sql.Date) {
                return ((java.sql.Date) date).toLocalDate();
            } 
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            return zdt.toLocalDate();
        }
        return null;
    }
    
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date != null) {
            Instant instant = date.toInstant();
            ZonedDateTime zdt = instant.atZone(ZoneId.systemDefault());
            return zdt.toLocalDateTime();
        }
        return null;
    }
    
    public static DateTimeFormatter getDateFormatter() {
        return getDateFormatter(java.util.Locale.getDefault());
    }
    
    public static DateTimeFormatter getDateFormatter(java.util.Locale locale) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                FormatStyle.SHORT, null, IsoChronology.INSTANCE, locale != null ? locale : java.util.Locale.getDefault());
        pattern = pattern.replace("yy", "yyyy");
        return DateTimeFormatter.ofPattern(pattern);
    }

    public static DateTimeFormatter getDateTimeFormatter(java.util.Locale locale) {
        String pattern = DateTimeFormatterBuilder.getLocalizedDateTimePattern(
                FormatStyle.SHORT, FormatStyle.MEDIUM, IsoChronology.INSTANCE, locale != null ? locale : java.util.Locale.getDefault());
        pattern = pattern.replace("yy", "yyyy");
        return DateTimeFormatter.ofPattern(pattern);
    }
    
    public static Date parseDateString(String inputDate) {


            Date outputDate = null;
            String[] possibleDateFormats =
                  {
                		"yyyy-MM-dd'T'HH:mm:ssXXX",
                        "yyyy.MM.dd G 'at' HH:mm:ss z",
                        "EEE, MMM d, ''yy",
                        "h:mm a",
                        "hh 'o''clock' a, zzzz",
                        "K:mm a, z",
                        "yyyyy.MMMMM.dd GGG hh:mm aaa",
                        "EEE, d MMM yyyy HH:mm:ss Z",
                        "yyMMddHHmmssZ",
                        "yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
                        "YYYY-'W'ww-u",
                        "EEE, dd MMM yyyy HH:mm:ss z", 
                        "EEE, dd MMM yyyy HH:mm zzzz",
                        "yyyy-MM-dd'T'HH:mm:ssZ",
                        "yyyy-MM-dd'T'HH:mm:ss.SSSzzzz", 
                        "yyyy-MM-dd'T'HH:mm:sszzzz",
                        "yyyy-MM-dd'T'HH:mm:ss z",
                        "yyyy-MM-dd'T'HH:mm:ssz", 
                        "yyyy-MM-dd'T'HH:mm:ss",
                        "yyyy-MM-dd'T'HHmmss.SSSz",
                        "yyyy-MM-dd",
                        "yyyyMMdd",
                        "dd/MM/yy",
                        "dd/MM/yyyy"
                  };

            try {

                outputDate = DateUtils.parseDate(inputDate, possibleDateFormats);
                System.out.println("inputDate ==> " + inputDate + ", outputDate ==> " + outputDate);

            } catch (ParseException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return outputDate;   	
    	
    }
    
}
