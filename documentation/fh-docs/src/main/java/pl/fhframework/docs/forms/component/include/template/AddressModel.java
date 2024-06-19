package pl.fhframework.docs.forms.component.include.template;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by krzysztof.kobylarek on 2016-12-21.
 */
public class AddressModel {

    @Getter @Setter
    private String city;

    @Getter @Setter
    private String country;

    @Getter @Setter
    private String province;

    @Getter @Setter
    private String streetNum;

    @Getter @Setter
    private String shippingInfo;

    @Getter @Setter
    private String giftInfo;

    @Getter
    private List<String> countries = Arrays.asList("-", "Poland", "Germany", "Spain" ,"France");

    @Getter
    private Map<String, List<String>> provinces = createProvinces();

    private static Map<String, List<String>> createProvinces(){
        Map<String, List<String>> provinces = new LinkedHashMap<String, List<String>>(){
            @Override
            public List<String> put(String key, List<String> value) {
                return super.put(key.toLowerCase(), value);
            }

            @Override
            public List<String> get(Object key) {
                return super.get(((String)key).toLowerCase());
            }
        };
        provinces.put("-", Arrays.asList("-"));
        provinces.put("France", Arrays.asList("Orléanais (Orléans)", "Normandy (Rouen)"));
        provinces.put("Germany", Arrays.asList("Baden-Württemberg", "Lower Saxony"));
        provinces.put("Poland", Arrays.asList("wielkopolskie", "mazowieckie"));
        provinces.put("Spain", Arrays.asList("Guadalajara", "Málaga"));
        return provinces;
    }

    public List<String> getProvincesByCountry(){
        if (country!=null)
            return provinces.get(country);
        return provinces.get("-");
    }

}
