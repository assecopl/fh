package pl.fhframework.model.forms.clock.service;

import java.time.LocalDate;

/**
 * Created by k.czajkowski on 16.02.2017.
 */

/**
 * Service responsible for giving names for given name day.
 */

public interface NameDayService {

    /**
     * Returns array of <code>String</code> with names associated with given <code>LocalDate</code>.
     *
     * @param date
     * @return
     */
    String[] getNames(LocalDate date);

    /**
     * Returns <code>String</code> witn names assosiated with given <code>LocalDate</code>. Multiple names are separated with comma.
     *
     * @param date
     * @return
     */
    String getNamesAsString(LocalDate date);
}
