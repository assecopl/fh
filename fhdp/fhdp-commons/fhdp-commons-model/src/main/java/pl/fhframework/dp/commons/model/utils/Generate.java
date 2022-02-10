package pl.fhframework.dp.commons.model.utils;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public final class Generate {

    public static String getRandomText(int count){
        return RandomStringUtils.randomAlphabetic(count);
    }

    public static synchronized String getRandomStringNumber(int count){
        return RandomStringUtils.randomNumeric(count);
    }
}
