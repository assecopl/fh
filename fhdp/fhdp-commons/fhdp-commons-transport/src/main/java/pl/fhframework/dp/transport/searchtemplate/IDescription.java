package pl.fhframework.dp.transport.searchtemplate;

import java.util.Locale;

public interface IDescription {
    default String getDescription() {
        return "???";
    }

    String getDescription(Locale var1);
}