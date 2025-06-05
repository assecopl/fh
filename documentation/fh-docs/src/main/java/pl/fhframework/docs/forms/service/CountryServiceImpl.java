package pl.fhframework.docs.forms.service;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import pl.fhframework.core.paging.ComparatorFunction;

import java.util.*;

/**
 * Created by k.czajkowski on 14.03.2017.
 */
@Service
public class CountryServiceImpl implements CountryService{

    private static List<Country> allCountries = new ArrayList<>(Arrays.asList(
            new Country("Albania", "Tirana", 2.831, 28.748),
            new Country("Andorra", "Andorra la Vella", 0.068, 0.468),
            new Country("Armenia", "Yerevan", 3.060, 29.743),
            new Country("Austria", "Vienna", 8.169, 83.858),
            new Country("Azerbaijan", "Baku", 9.165, 86.600),
            new Country("Belarus", "Minsk", 9.458, 207.560),
            new Country("Belgium", "Brussels", 11.250, 30.528),
            new Country("Bosnia and Herzegovina", "Sarajevo", 3.843, 51.129),
            new Country("Bulgaria", "Sofia", 7.621, 110.910),
            new Country("Croatia", "Zagreb", 4.437, 56.542),
            new Country("Cyprus", "Nicosia", 0.778, 9.251),
            new Country("Czech", "Prague", 10.553, 78.866),
            new Country("Denmark", "Copenhagen", 5.748, 43.094),
            new Country("Estonia", "Tallinn", 1.340, 45.226),
            new Country("Finland", "Helsinki", 5.157, 336.593),
            new Country("France", "Paris",66.104, 547.030 ),
            new Country("Georgia", "Tbilisi", 4.661, 69.700),
            new Country("Germany", "Berlin", 80.716, 357.021),
            new Country("Greece", "Athens", 11.123, 131.957),
            new Country("Hungary", "Budapest", 10.075, 93.030),
            new Country("Iceland", "Reykjavik", 0.332, 102.775),
            new Country("Ireland", "Dublin", 4.234, 70.280),
            new Country("Italy", "Rome", 60.655, 301.230),
            new Country("Kazakhstan", "Astana", 15.217, 2724.900),
            new Country("Latvia", "Riga", 2.067, 64.589),
            new Country("Liechtenstein", "Vaduz", 0.032, 0.160),
            new Country("Lithuania", "Vilnius", 2.988, 65.300),
            new Country("Luxembourg", "Luxembourg City", 0.448, 2.586),
            new Country("Macedonia", "Skopje", 2.054, 25.713),
            new Country("Malta", "Valletta", 0.397, 0.316),
            new Country("Moldova", "Chisinau", 4.434, 33.843),
            new Country("Monaco", "Monaco", 0.031, 0.002),
            new Country("Montenegro", "Podgorica", 0.616, 13.812),
            new Country("Netherlands", "Amsterdam", 16.902, 41.526),
            new Country("Norway", "Oslo", 5.018, 385.178),
            new Country("Poland", "Warsaw", 38.625, 312.685),
            new Country("Portugal", "Lisbon", 10.409, 91.568),
            new Country("Romania", "Bucharest", 21.698, 238.391),
            new Country("Russia", "Moscow", 143.975, 17075.400),
            new Country("San Marino", "San Marino", 0.027, 0.061),
            new Country("Serbia", "Belgrade", 7.120, 88.361),
            new Country("Slovakia", "Bratislava", 5.422, 48.845),
            new Country("Slovenia", "Ljubljana", 2.050, 20.273),
            new Country("Spain", "Madrid", 47.059, 504.851),
            new Country("Sweden", "Stockholm", 9.090, 449.964),
            new Country("Switzerland", "Bern", 7.507, 41.290),
            new Country("Turkey", "Ankara", 77.695, 783.562),
            new Country("Ukraine", "Kiev", 45.360, 603.700),
            new Country("United Kingdom", "London", 65.110, 242.495)
    ));


    private List<Country> instanceCountries = new ArrayList<>(allCountries);

    private enum SortedProperty {

        CountryName((firstCountry, secondCountry) -> firstCountry.getName().compareTo(secondCountry.getName())),
        CountryCapital((firstCountry, secondCountry) -> firstCountry.getCapital().compareTo(secondCountry.getCapital())),
        CountryArea((firstCountry, secondCountry) -> Double.compare(firstCountry.getArea(), secondCountry.getArea())),
        CountryPopulation((firstCountry, secondCountry) -> Double.compare(firstCountry.getPopulation(), secondCountry.getPopulation()));

        private ComparatorFunction<Country> comparator;

        SortedProperty(ComparatorFunction<Country> comparator) {
            this.comparator = comparator;
        }
    }

    @Override
    public Page<Country> createPage(Pageable pageable) {
        int startingPosition = pageable.getPageNumber() * pageable.getPageSize();
        List<Country> countriesCopy = new ArrayList<>(instanceCountries);
        if (pageable.getSort() != null && pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            if (order != null) {
                SortedProperty sortedProperty = SortedProperty.valueOf(order.getProperty());
                Collections.sort(countriesCopy, (first, second) -> sortedProperty.comparator.compare(first, second, order.getDirection()));
            }
        }
        List<Country> filteredCountries = new LinkedList<>();
        for (int i = startingPosition; i < Integer.min(startingPosition + pageable.getPageSize(), countriesCopy.size()); i++) {
            filteredCountries.add(countriesCopy.get(i));
        }
        return new PageImpl<>(filteredCountries, pageable, countriesCopy.size());
    }

    @Override
    public void remove(Country country) {
        instanceCountries.remove(country);
    }

    @Override
    public void removeAll(List<Country> countries) {
        instanceCountries.removeAll(countries);
    }

    @Override
    public void resetCountries() {
        instanceCountries = new ArrayList<>(allCountries);
    }

    public List<Country> findAll() {
        return  allCountries;
    }
}
