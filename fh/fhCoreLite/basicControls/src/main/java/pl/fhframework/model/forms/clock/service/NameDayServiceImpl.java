package pl.fhframework.model.forms.clock.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Created by k.czajkowski on 16.02.2017.
 */
@Service
public class NameDayServiceImpl implements NameDayService {

    private static final String COMMA_SEPARATOR = ",";

    @Value("classpath:clock/names.properties")
    private Resource namesFile;

    private Map<String, String[]> dayNamesMap;

    @Override
    public String[] getNames(LocalDate date) {
        if (dayNamesMap == null) {
            return null;
        }
        return dayNamesMap.get(getDayAndMonth(date));
    }

    private String getDayAndMonth(LocalDate date) {
        int dayOfMonth = date.getDayOfMonth();
        int monthValue = date.getMonthValue();
        StringBuilder sb = new StringBuilder();
        if (dayOfMonth < 10) {
            sb.append(0);
        }
        sb.append(dayOfMonth);
        if (monthValue < 10) {
            sb.append(0);
        }
        sb.append(monthValue);
        return sb.toString();
    }

    @Override
    public String getNamesAsString(LocalDate date) {
        String[] dayNames = getNames(date);
        if (dayNames == null) {
            FhLogger.debug(this.getClass(), logger -> logger.log("Did not find find names for {} ", date.toString()));
            return null;
        }
        String dayNamesString = Arrays.toString(dayNames);
        return dayNamesString.substring(1, dayNamesString.length() - 1);
    }

    @PostConstruct
    public void init() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(namesFile.getInputStream(), "UTF8"))) {
            Properties prop = new Properties();
            prop.load(reader);
            dayNamesMap = prop.entrySet().stream().collect(Collectors.toMap(entry ->  entry.getKey().toString(), entry -> entry.getValue().toString().split(COMMA_SEPARATOR)));
        } catch (IOException exception) {
            FhLogger.error("Error during reading {} file.", namesFile.getFilename(), exception);
        }
    }
}
