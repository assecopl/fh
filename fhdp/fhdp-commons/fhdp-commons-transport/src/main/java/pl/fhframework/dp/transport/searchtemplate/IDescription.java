package pl.fhframework.dp.transport.searchtemplate;

import java.util.Locale;

//TODO: REMOVE AFTER FRONT DEPENDENCIES ARE FIXED
public interface IDescription {
    default String getDescription() {
        return "JAPIS BASED, TODO"; //this.getDescription(LocaleAccessUtil.getLocale());
    }

    String getDescription(Locale var1);
}