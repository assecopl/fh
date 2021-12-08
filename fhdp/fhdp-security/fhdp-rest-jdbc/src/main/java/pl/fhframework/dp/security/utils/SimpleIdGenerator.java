package pl.fhframework.dp.security.utils;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @author Tomasz Kozlowski (created on 29.01.2021)
 */
public class SimpleIdGenerator {

    private static int index = -1;

    private final static DecimalFormat SUFFIX_FORMAT = new DecimalFormat("000000");
    private final static String ID_FORMAT = "%s%s";

    private final static List<Integer> RANDOM_INTEGERS;
    static {
        RANDOM_INTEGERS = IntStream.range(0, 999999)
                .boxed()
                .collect(Collectors.toList());
        Collections.shuffle(RANDOM_INTEGERS);
    }

    public static Long generateId() {
        Long id = System.currentTimeMillis();
        int uniqueInt = getNextUniqueInt();
        return Long.valueOf(String.format(ID_FORMAT, id, SUFFIX_FORMAT.format(uniqueInt)));
    }

    private static int getNextUniqueInt() {
        index++;
        if (index >= RANDOM_INTEGERS.size()) {
            index = 0;
        }
        return RANDOM_INTEGERS.get(index);
    }

}
