package pl.fhframework.docs.forms.service;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Created by k.czajkowski on 14.03.2017.
 */

public interface CountryService {

    /**
     * Creates Page based on pageable
     *
     * @param pageable
     * @return
     */
    Page<Country> createPage(Pageable pageable);

    /**
     * Removes country
     *
     * @param country - country to remove
     */
    void remove(Country country);

    /**
     * Remove countries
     *
     * @param countries - countries to remove
     */
    void removeAll(List<Country> countries);

    /**
     * Loads again all countries
     */
    void resetCountries();

    @Getter
    @AllArgsConstructor
    public static class Country {
        private String name;
        private String capital;
        private double population;
        private double area;
    }

    List<Country> findAll();
}
