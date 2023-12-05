package pl.fhframework.fhdp.example.lookup.provider;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.model.forms.PageModel;
import pl.fhframework.model.forms.provider.IDictionaryLookupProvider;
import pl.fhframework.model.forms.provider.NameValue;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class CountryNameValueLookupDummyProvider implements /*IComboDataProviderFhDP<NameValueDto, String>,*/ IDictionaryLookupProvider<NameValueDto, String> {


    @Override
    public String getDisplayValue(NameValueDto element) {
        if (element!=null) {
            return element.getValue() + ": " + element.getName();
        }else{
            return "";
        }
    }

    @Override
    public String getDisplayCode(String value) {
        return value;
    }

    @Override
    public String getDisplayValueByElement(Object object) {
        try {
            return getDisplayValue((NameValueDto) object);
        }catch (ClassCastException exc){
            FhLogger.error("Provided object must be ", exc);
            throw exc;
        }
    }

//    @Override
//    public String getDisplayValueByElementId(String elementId) {
//        return getDisplayValue(getElementById(elementId));
//    }

    @Override
    public String getModelValue(NameValueDto element) {
        return element.getName();
    }

    @Override
    public NameValueDto getElementByModelValue(String modelValue, Function<String, Object> externalAttributesResolver) {
        String regionFilter = (String)externalAttributesResolver.apply("regionFilter");
        return Arrays.stream(CountryEnum.values())
                .filter(country-> (regionFilter==null||regionFilter.isEmpty()) || regionFilter.equals(country.region))
                .filter(country -> country.codeAlfa2.equals(modelValue) || country.codeAlfa3.equals(modelValue))
                .map(country -> new NameValueDto(country.codeAlfa2, country.polishName))
                .findFirst().orElse(null);
    }

//    @Override
//    public Boolean areObjectsEquals(String element1, String element2) {
//        return Objects.equals(element1, element2);
//    }

    @Override
    public PageModel<NameValueDto> getDictionaryElementsPaged(String searchText, Pageable pageable, Function<String, Object> externalAttributesResolver) {
        String regionFilter = (String)externalAttributesResolver.apply("regionFilter");
        List<NameValueDto> countries = Arrays.stream(CountryEnum.values())
                .filter(country-> (regionFilter==null||regionFilter.isEmpty()) || regionFilter.equals(country.region))
                .filter(country -> country.isMatchingByName(searchText))
                .map(country -> new NameValueDto(country.codeAlfa2, country.polishName))
                .collect(Collectors.toList());
        return new CountryNameValuePagedTableSource(countries).createPagedModel(pageable);
    }

    @Override
    public List<NameValue> getColumnDefinitions() {
        List<NameValue> columns = new ArrayList<>();
        columns.add(new NameValue("Value", "value"));
        columns.add(new NameValue("Name/Code", "name"));
        return columns;
    }

//    @Override
//    public String initResultFromKey(String key) {
//        return key;
//    }

//    @Override
//    public String getSrcKey(NameValueDto element) {
//        if (element == null) return null;
//        return element.getValue();
//    }


    private NameValueDto getValue(String code) {
        return Arrays.stream(CountryEnum.values())
                .filter(countryEnum -> countryEnum.isMatchingByCodeStrict(code, true))
                .findFirst()
                .map(country -> new NameValueDto(country.codeAlfa2, country.polishName))
                .orElseGet(() -> Arrays.stream(CountryEnum.values())
                        .filter(countryEnum -> countryEnum.isMatchingByCodeStartsWith(code, true))
                        .findFirst()
                        .map(country -> new NameValueDto(country.codeAlfa2, country.polishName))
                        .orElseGet(() -> null));
    }

    public String getTitle(Function<String, Object> externalAttributesValuesProvider) {
        return "Countries";
    }

    @Getter
    @AllArgsConstructor
    public enum CountryEnum {
        AFGANISTAN("Afganistan", "Afghanistan", "AF", "AFG", "004", "ISO 3166-2:AF", "nonUE"),
        ALBANIA("Albania", "Albania", "AL", "ALB", "008", "ISO 3166-2:AL", "nonUE"),
        ALGIERIA("Algieria", "Algeria", "DZ", "DZA", "012", "ISO 3166-2:DZ", "nonUE"),
        ANDORA("Andora", "Andorra", "AD", "AND", "020", "ISO 3166-2:AD", "nonUE"),
        ANGOLA("Angola", "Angola", "AO", "AGO", "024", "ISO 3166-2:AO", "nonUE"),
        ANGUILLA("Anguilla", "Anguilla", "AI", "AIA", "660", "ISO 3166-2:AI", "nonUE"),
        ANTARKTYDA("Antarktyda", "Antarctica", "AQ", "ATA", "010", "ISO 3166-2:AQ", "nonUE"),
        ANTIGUA_I_BARBUDA("Antigua i Barbuda", "Antigua and Barbuda", "AG", "ATG", "028", "ISO 3166-2:AG", "nonUE"),
        ARABIA_SAUDYJSKA("Arabia Saudyjska", "Saudi Arabia", "SA", "SAU", "682", "ISO 3166-2:SA", "nonUE"),
        ARGENTYNA("Argentyna", "Argentina", "AR", "ARG", "032", "ISO 3166-2:AR", "nonUE"),
        ARMENIA("Armenia", "Armenia", "AM", "ARM", "051", "ISO 3166-2:AM", "nonUE"),
        ARUBA("Aruba", "Aruba", "AW", "ABW", "533", "ISO 3166-2:AW", "nonUE"),
        AUSTRALIA("Australia", "Australia", "AU", "AUS", "036", "ISO 3166-2:AU", "nonUE"),
        AUSTRIA("Austria", "Austria", "AT", "AUT", "040", "ISO 3166-2:AT", "UE"),
        AZERBEJDZAN("Azerbejdżan", "Azerbaijan", "AZ", "AZE", "031", "ISO 3166-2:AZ", "nonUE"),
        BAHAMY("Bahamy", "Bahamas", "BS", "BHS", "044", "ISO 3166-2:BS", "nonUE"),
        BAHRAJN("Bahrajn", "Bahrain", "BH", "BHR", "048", "ISO 3166-2:BH", "nonUE"),
        BANGLADESZ("Bangladesz", "Bangladesh", "BD", "BGD", "050", "ISO 3166-2:BD", "nonUE"),
        BARBADOS("Barbados", "Barbados", "BB", "BRB", "052", "ISO 3166-2:BB", "nonUE"),
        BELGIA("Belgia", "Belgium", "BE", "BEL", "056", "ISO 3166-2:BE", "UE"),
        BELIZE("Belize", "Belize", "BZ", "BLZ", "084", "ISO 3166-2:BZ", "nonUE"),
        BENIN("Benin", "Benin", "BJ", "BEN", "204", "ISO 3166-2:BJ", "nonUE"),
        BERMUDY("Bermudy", "Bermuda", "BM", "BMU", "060", "ISO 3166-2:BM", "nonUE"),
        BHUTAN("Bhutan", "Bhutan", "BT", "BTN", "064", "ISO 3166-2:BT", "nonUE"),
        BIALORUS("Białoruś", "Belarus", "BY", "BLR", "112", "ISO 3166-2:BY", "nonUE"),
        BIRMA("Birma", "Myanmar", "MM", "MMR", "104", "ISO 3166-2:MM", "nonUE"),
        BOLIWIA("Boliwia", "Bolivia, Plurinational State of", "BO", "BOL", "068", "ISO 3166-2:BO", "nonUE"),
        BONAIRE_SINT_EUSTATIUS_I_SABA("Bonaire, Sint Eustatius i Saba", "Bonaire, Saint Eustatius and Saba", "BQ", "BES", "535", "ISO 3166-2:BQ", "nonUE"),
        BOSNIA_I_HERCEGOWINA("Bośnia i Hercegowina", "Bosnia and Herzegovina", "BA", "BIH", "070", "ISO 3166-2:BA", "nonUE"),
        BOTSWANA("Botswana", "Botswana", "BW", "BWA", "072", "ISO 3166-2:BW", "nonUE"),
        BRAZYLIA("Brazylia", "Brazil", "BR", "BRA", "076", "ISO 3166-2:BR", "nonUE"),
        BRUNEI("Brunei", "Brunei Darussalam", "BN", "BRN", "096", "ISO 3166-2:BN", "nonUE"),
        BRYTYJSKIE_TERYTORIUM_OCEANU_INDYJSKIEGO("Brytyjskie Terytorium Oceanu Indyjskiego", "British Indian Ocean Territory", "IO", "IOT", "086", "ISO 3166-2:IO", "nonUE"),
        BRYTYJSKIE_WYSPY_DZIEWICZE("Brytyjskie Wyspy Dziewicze", "Virgin Islands, British", "VG", "VGB", "092", "ISO 3166-2:VG", "nonUE"),
        BULGARIA("Bułgaria", "Bulgaria", "BG", "BGR", "100", "ISO 3166-2:BG", "UE"),
        BURKINA_FASO("Burkina Faso", "Burkina Faso", "BF", "BFA", "854", "ISO 3166-2:BF", "nonUE"),
        BURUNDI("Burundi", "Burundi", "BI", "BDI", "108", "ISO 3166-2:BI", "nonUE"),
        CHILE("Chile", "Chile", "CL", "CHL", "152", "ISO 3166-2:CL", "nonUE"),
        CHINY("Chiny", "China", "CN", "CHN", "156", "ISO 3166-2:CN", "nonUE"),
        CHORWACJA("Chorwacja", "Croatia", "HR", "HRV", "191", "ISO 3166-2:HR", "UE"),
        CURACAO("Curaçao", "Curaçao", "CW", "CUW", "531", "ISO 3166-2:CW", "nonUE"),
        CYPR("Cypr", "Cyprus", "CY", "CYP", "196", "ISO 3166-2:CY", "UE"),
        CZAD("Czad", "Chad", "TD", "TCD", "148", "ISO 3166-2:TD", "nonUE"),
        CZARNOGORA("Czarnogóra", "Montenegro", "ME", "MNE", "499", "ISO 3166-2:ME", "nonUE"),
        CZECHY("Czechy", "Czech Republic", "CZ", "CZE", "203", "ISO 3166-2:CZ", "UE"),
        DALEKIE_WYSPY_MNIEJSZE_STANOW_ZJEDNOCZONYCH("Dalekie Wyspy Mniejsze Stanów Zjednoczonych", "United States Minor Outlying Islands", "UM", "UMI", "581", "ISO 3166-2:UM", "nonUE"),
        DANIA("Dania", "Denmark", "DK", "DNK", "208", "ISO 3166-2:DK", "UE"),
        DEMOKRATYCZNA_REPUBLIKA_KONGA("Demokratyczna Republika Konga", "Congo, the Democratic Republic of the", "CD", "COD", "180", "ISO 3166-2:CD", "nonUE"),
        DOMINIKA("Dominika", "Dominica", "DM", "DMA", "212", "ISO 3166-2:DM", "nonUE"),
        DOMINIKANA("Dominikana", "Dominican Republic", "DO", "DOM", "214", "ISO 3166-2:DO", "nonUE"),
        DZIBUTI("Dżibuti", "Djibouti", "DJ", "DJI", "262", "ISO 3166-2:DJ", "nonUE"),
        EGIPT("Egipt", "Egypt", "EG", "EGY", "818", "ISO 3166-2:EG", "nonUE"),
        EKWADOR("Ekwador", "Ecuador", "EC", "ECU", "218", "ISO 3166-2:EC", "nonUE"),
        ERYTREA("Erytrea", "Eritrea", "ER", "ERI", "232", "ISO 3166-2:ER", "nonUE"),
        ESTONIA("Estonia", "Estonia", "EE", "EST", "233", "ISO 3166-2:EE", "UE"),
        ETIOPIA("Etiopia", "Ethiopia", "ET", "ETH", "231", "ISO 3166-2:ET", "nonUE"),
        FALKLANDY("Falklandy", "Falkland Islands (Malvinas)", "FK", "FLK", "238", "ISO 3166-2:FK", "nonUE"),
        FIDZI("Fidżi", "Fiji", "FJ", "FJI", "242", "ISO 3166-2:FJ", "nonUE"),
        FILIPINY("Filipiny", "Philippines", "PH", "PHL", "608", "ISO 3166-2:PH", "nonUE"),
        FINLANDIA("Finlandia", "Finland", "FI", "FIN", "246", "ISO 3166-2:FI", "UE"),
        FRANCJA("Francja", "France", "FR", "FRA", "250", "ISO 3166-2:FR", "UE"),
        FRANCUSKIE_TERYTORIA_POLUDNIOWE_I_ANTARKTYCZNE("Francuskie Terytoria Południowe i Antarktyczne", "French Southern Territories", "TF", "ATF", "260", "ISO 3166-2:TF", "nonUE"),
        GABON("Gabon", "Gabon", "GA", "GAB", "266", "ISO 3166-2:GA", "nonUE"),
        GAMBIA("Gambia", "Gambia", "GM", "GMB", "270", "ISO 3166-2:GM", "nonUE"),
        GEORGIA_POLUDNIOWA_I_SANDWICH_POLUDNIOWY("Georgia Południowa i Sandwich Południowy", "South Georgia and the South Sandwich Islands", "GS", "SGS", "239", "ISO 3166-2:GS", "nonUE"),
        GHANA("Ghana", "Ghana", "GH", "GHA", "288", "ISO 3166-2:GH", "nonUE"),
        GIBRALTAR("Gibraltar", "Gibraltar", "GI", "GIB", "292", "ISO 3166-2:GI", "nonUE"),
        GRECJA("Grecja", "Greece", "GR", "GRC", "300", "ISO 3166-2:GR", "UE"),
        GRENADA("Grenada", "Grenada", "GD", "GRD", "308", "ISO 3166-2:GD", "nonUE"),
        GRENLANDIA("Grenlandia", "Greenland", "GL", "GRL", "304", "ISO 3166-2:GL", "nonUE"),
        GRUZJA("Gruzja", "Georgia", "GE", "GEO", "268", "ISO 3166-2:GE", "nonUE"),
        GUAM("Guam", "Guam", "GU", "GUM", "316", "ISO 3166-2:GU", "nonUE"),
        GUERNSEY("Guernsey", "Guernsey", "GG", "GGY", "831", "ISO 3166-2:GG", "nonUE"),
        GUJANA_FRANCUSKA("Gujana Francuska", "French Guiana", "GF", "GUF", "254", "ISO 3166-2:GF", "nonUE"),
        GUJANA("Gujana", "Guyana", "GY", "GUY", "328", "ISO 3166-2:GY", "nonUE"),
        GWADELUPA("Gwadelupa", "Guadeloupe", "GP", "GLP", "312", "ISO 3166-2:GP", "nonUE"),
        GWATEMALA("Gwatemala", "Guatemala", "GT", "GTM", "320", "ISO 3166-2:GT", "nonUE"),
        GWINEA_BISSAU("Gwinea Bissau", "Guinea-Bissau", "GW", "GNB", "624", "ISO 3166-2:GW", "nonUE"),
        GWINEA_ROWNIKOWA("Gwinea Równikowa", "Equatorial Guinea", "GQ", "GNQ", "226", "ISO 3166-2:GQ", "nonUE"),
        GWINEA("Gwinea", "Guinea", "GN", "GIN", "324", "ISO 3166-2:GN", "nonUE"),
        HAITI("Haiti", "Haiti", "HT", "HTI", "332", "ISO 3166-2:HT", "nonUE"),
        HISZPANIA("Hiszpania", "Spain", "ES", "ESP", "724", "ISO 3166-2:ES", "UE"),
        HOLANDIA("Holandia", "Netherlands", "NL", "NLD", "528", "ISO 3166-2:NL", "UE"),
        HONDURAS("Honduras", "Honduras", "HN", "HND", "340", "ISO 3166-2:HN", "nonUE"),
        HONGKONG("Hongkong", "Hong Kong", "HK", "HKG", "344", "ISO 3166-2:HK", "nonUE"),
        INDIE("Indie", "India", "IN", "IND", "356", "ISO 3166-2:IN", "nonUE"),
        INDONEZJA("Indonezja", "Indonesia", "ID", "IDN", "360", "ISO 3166-2:ID", "nonUE"),
        IRAK("Irak", "Iraq", "IQ", "IRQ", "368", "ISO 3166-2:IQ", "nonUE"),
        IRAN("Iran", "Iran, Islamic Republic of", "IR", "IRN", "364", "ISO 3166-2:IR", "nonUE"),
        IRLANDIA("Irlandia", "Ireland", "IE", "IRL", "372", "ISO 3166-2:IE", "UE"),
        ISLANDIA("Islandia", "Iceland", "IS", "ISL", "352", "ISO 3166-2:IS", "nonUE"),
        IZRAEL("Izrael", "Israel", "IL", "ISR", "376", "ISO 3166-2:IL", "nonUE"),
        JAMAJKA("Jamajka", "Jamaica", "JM", "JAM", "388", "ISO 3166-2:JM", "nonUE"),
        JAPONIA("Japonia", "Japan", "JP", "JPN", "392", "ISO 3166-2:JP", "nonUE"),
        JEMEN("Jemen", "Yemen", "YE", "YEM", "887", "ISO 3166-2:YE", "nonUE"),
        JERSEY("Jersey", "Jersey", "JE", "JEY", "832", "ISO 3166-2:JE", "nonUE"),
        JORDANIA("Jordania", "Jordan", "JO", "JOR", "400", "ISO 3166-2:JO/td>", "nonUE"),
        KAJMANY("Kajmany", "Cayman Islands", "KY", "CYM", "136", "ISO 3166-2:KY", "nonUE"),
        KAMBODZA("Kambodża", "Cambodia", "KH", "KHM", "116", "ISO 3166-2:KH", "nonUE"),
        KAMERUN("Kamerun", "Cameroon", "CM", "CMR", "120", "ISO 3166-2:CM", "nonUE"),
        KANADA("Kanada", "Canada", "CA", "CAN", "124", "ISO 3166-2:CA", "nonUE"),
        KATAR("Katar", "Qatar", "QA", "QAT", "634", "ISO 3166-2:QA", "nonUE"),
        KAZACHSTAN("Kazachstan", "Kazakhstan", "KZ", "KAZ", "398", "ISO 3166-2:KZ<", "nonUE"),
        KENIA("Kenia", "Kenya", "KE", "KEN", "404", "ISO 3166-2:KE", "nonUE"),
        KIRGISTAN("Kirgistan", "Kyrgyzstan", "KG", "KGZ", "417", "ISO 3166-2:KG", "nonUE"),
        KIRIBATI("Kiribati", "Kiribati", "KI", "KIR", "296", "ISO 3166-2:KI", "nonUE"),
        KOLUMBIA("Kolumbia", "Colombia", "CO", "COL", "170", "ISO 3166-2:CO", "nonUE"),
        KOMORY("Komory", "Comoros", "KM", "COM", "174", "ISO 3166-2:KM", "nonUE"),
        KONGO("Kongo", "Congo", "CG", "COG", "178", "ISO 3166-2:CG", "nonUE"),
        KOREA_POLUDNIOWA("Korea Południowa", "Korea, Republic of", "KR", "KOR", "410", "ISO 3166-2:KR", "nonUE"),
        KOREA_POLNOCNA("Korea Północna", "Korea, Democratic People’s Republic of", "KP", "PRK", "408", "ISO 3166-2:KP", "nonUE"),
        KOSTARYKA("Kostaryka", "Costa Rica", "CR", "CRI", "188", "ISO 3166-2:CR", "nonUE"),
        KUBA("Kuba", "Cuba", "CU", "CUB", "192", "ISO 3166-2:CU", "nonUE"),
        KUWEJT("Kuwejt", "Kuwait", "KW", "KWT", "414", "ISO 3166-2:KW", "nonUE"),
        LAOS("Laos", "Lao People’s Democratic Republic", "LA", "LAO", "418", "ISO 3166-2:LA", "nonUE"),
        LESOTHO("Lesotho", "Lesotho", "LS", "LSO", "426", "ISO 3166-2:LS", "nonUE"),
        LIBAN("Liban", "Lebanon", "LB", "LBN", "422", "ISO 3166-2:LB", "nonUE"),
        LIBERIA("Liberia", "Liberia", "LR", "LBR", "430", "ISO 3166-2:LR", "nonUE"),
        LIBIA("Libia", "Libyan Arab Jamahiriya", "LY", "LBY", "434", "ISO 3166-2:LY", "nonUE"),
        LIECHTENSTEIN("Liechtenstein", "Liechtenstein", "LI", "LIE", "438", "ISO 3166-2:LI", "nonUE"),
        LITWA("Litwa", "Lithuania", "LT", "LTU", "440", "ISO 3166-2:LT", "UE"),
        LUKSEMBURG("Luksemburg", "Luxembourg", "LU", "LUX", "442", "ISO 3166-2:LU", "UE"),
        LOTWA("Łotwa", "Latvia", "LV", "LVA", "428", "ISO 3166-2:LV", "UE"),
        MACEDONIA("Macedonia", "Macedonia, the former Yugoslav Republic of", "MK", "MKD", "807", "ISO 3166-2:MK", "nonUE"),
        MADAGASKAR("Madagaskar", "Madagascar", "MG", "MDG", "450", "ISO 3166-2:MG", "nonUE"),
        MAJOTTA("Majotta", "Mayotte", "YT", "MYT", "175", "ISO 3166-2:YT", "nonUE"),
        MAKAU("Makau", "Macao", "MO", "MAC", "446", "ISO 3166-2:MO", "nonUE"),
        MALAWI("Malawi", "Malawi", "MW", "MWI", "454", "ISO 3166-2:MW", "nonUE"),
        MALEDIWY("Malediwy", "Maldives", "MV", "MDV", "462", "ISO 3166-2:MV", "nonUE"),
        MALEZJA("Malezja", "Malaysia", "MY", "MYS", "458", "ISO 3166-2:MY", "nonUE"),
        MALI("Mali", "Mali", "ML", "MLI", "466", "ISO 3166-2:ML", "nonUE"),
        MALTA("Malta", "Malta", "MT", "MLT", "470", "ISO 3166-2:MT", "UE"),
        MARIANY_POLNOCNE("Mariany Północne", "Northern Mariana Islands", "MP", "MNP", "580", "ISO 3166-2:MP", "nonUE"),
        MAROKO("Maroko", "Morocco", "MA", "MAR", "504", "ISO 3166-2:MA", "nonUE"),
        MARTYNIKA("Martynika", "Martinique", "MQ", "MTQ", "474", "ISO 3166-2:MQ", "nonUE"),
        MAURETANIA("Mauretania", "Mauritania", "MR", "MRT", "478", "ISO 3166-2:MR", "nonUE"),
        MAURITIUS("Mauritius", "Mauritius", "MU", "MUS", "480", "ISO 3166-2:MU", "nonUE"),
        MEKSYK("Meksyk", "Mexico", "MX", "MEX", "484", "ISO 3166-2:MX", "nonUE"),
        MIKRONEZJA("Mikronezja", "Micronesia, Federated States of", "FM", "FSM", "583", ">ISO 3166-2:FM", "nonUE"),
        MOLDAWIA("Mołdawia", "Moldova, Republic of", "MD", "MDA", "498", "ISO 3166-2:MD", "nonUE"),
        MONAKO("Monako", "Monaco", "MC", "MCO", "492", "ISO 3166-2:MC", "nonUE"),
        MONGOLIA("Mongolia", "Mongolia", "MN", "MNG", "496", "ISO 3166-2:MN", "nonUE"),
        MONTSERRAT("Montserrat", "Montserrat", "MS", "MSR", "500", "ISO 3166-2:MS", "nonUE"),
        MOZAMBIK("Mozambik", "Mozambique", "MZ", "MOZ", "508", "ISO 3166-2:MZ", "nonUE"),
        NAMIBIA("Namibia", "Namibia", "NA", "NAM", "516", "ISO 3166-2:NA", "nonUE"),
        NAURU("Nauru", "Nauru", "NR", "NRU", "520", "ISO 3166-2:NR", "nonUE"),
        NEPAL("Nepal", "Nepal", "NP", "NPL", "524", "ISO 3166-2:NP", "nonUE"),
        NIEMCY("Niemcy", "Germany", "DE", "DEU", "276", "ISO 3166-2:DE", "UE"),
        NIGER("Niger", "Niger", "NE", "NER", "562", "ISO 3166-2:NE", "nonUE"),
        NIGERIA("Nigeria", "Nigeria", "NG", "NGA", "566", "ISO 3166-2:NG", "nonUE"),
        NIKARAGUA("Nikaragua", "Nicaragua", "NI", "NIC", "558", "ISO 3166-2:NI", "nonUE"),
        NIUE("Niue", "Niue", "NU", "NIU", "570", "ISO 3166-2:NU", "nonUE"),
        NORFOLK("Norfolk", "Norfolk Island", "NF", "NFK", "574", "ISO 3166-2:NF", "nonUE"),
        NORWEGIA("Norwegia", "Norway", "NO", "NOR", "578", "ISO 3166-2:NO", "nonUE"),
        NOWA_KALEDONIA("Nowa Kaledonia", "New Caledonia", "NC", "NCL", "540", "ISO 3166-2:NC", "nonUE"),
        NOWA_ZELANDIA("Nowa Zelandia", "New Zealand", "NZ", "NZL", "554", "ISO 3166-2:NZ", "nonUE"),
        OMAN("Oman", "Oman", "OM", "OMN", "512", "ISO 3166-2:OM", "nonUE"),
        PAKISTAN("Pakistan", "Pakistan", "PK", "PAK", "586", "ISO 3166-2:PK", "nonUE"),
        PALAU("Palau", "Palau", "PW", "PLW", "585", "ISO 3166-2:PW", "nonUE"),
        PALESTYNA("Palestyna", "Palestinian Territory, Occupied", "PS", "PSE", "275", "ISO 3166-2:PS", "nonUE"),
        PANAMA("Panama", "Panama", "PA", "PAN", "591", "ISO 3166-2:PA", "nonUE"),
        PAPUA_NOWA_GWINEA("Papua-Nowa Gwinea", "Papua New Guinea", "PG", "PNG", "598", "ISO 3166-2:PG", "nonUE"),
        PARAGWAJ("Paragwaj", "Paraguay", "PY", "PRY", "600", "ISO 3166-2:PY", "nonUE"),
        PERU("Peru", "Peru", "PE", "PER", "604", "ISO 3166-2:PE", "nonUE"),
        PITCAIRN("Pitcairn", "Pitcairn", "PN", "PCN", "612", "ISO 3166-2:PN", "nonUE"),
        POLINEZJA_FRANCUSKA("Polinezja Francuska", "French Polynesia", "PF", "PYF", "258", "ISO 3166-2:PF", "nonUE"),
        POLSKA("Polska", "Poland", "PL", "POL", "616", "ISO 3166-2:PL", "UE"),
        PORTORYKO("Portoryko", "Puerto Rico", "PR", "PRI", "630", "ISO 3166-2:PR", "nonUE"),
        PORTUGALIA("Portugalia", "Portugal", "PT", "PRT", "620", "ISO 3166-2:PT", "UE"),
        TAJWAN("Tajwan", "Taiwan", "TW", "TWN", "158", "ISO 3166-2:TW", "nonUE"),
        REPUBLIKA_POLUDNIOWEJ_AFRYKI("Republika Południowej Afryki", "South Africa", "ZA", "ZAF", "710", "ISO 3166-2:ZA", "nonUE"),
        REPUBLIKA_SRODKOWOAFRYKANSKA("Republika Środkowoafrykańska", "Central African Republic", "CF", "CAF", "140", "ISO 3166-2:CF", "nonUE"),
        REPUBLIKA_ZIELONEGO_PRZYLADKA("Republika Zielonego Przylądka", "Cape Verde", "CV", "CPV", "132", "ISO 3166-2:CV", "nonUE"),
        REUNION("Reunion", "Réunion", "RE", "REU", "638", "ISO 3166-2:RE", "nonUE"),
        ROSJA("Rosja", "Russian Federation", "RU", "RUS", "643", "ISO 3166-2:RU", "nonUE"),
        RUMUNIA("Rumunia", "Romania", "RO", "ROU", "642", "ISO 3166-2:RO", "UE"),
        RWANDA("Rwanda", "Rwanda", "RW", "RWA", "646", "ISO 3166-2:RW", "nonUE"),
        SAHARA_ZACHODNIA("Sahara Zachodnia", "Western Sahara", "EH", "ESH", "732", "ISO 3166-2:EH", "nonUE"),
        SAINT_KITTS_I_NEVIS("Saint Kitts i Nevis", "Saint Kitts and Nevis", "KN", "KNA", "659", "ISO 3166-2:KN", "nonUE"),
        SAINT_LUCIA("Saint Lucia", "Saint Lucia", "LC", "LCA", "662", "ISO 3166-2:LC", "nonUE"),
        SAINT_VINCENT_I_GRENADYNY("Saint Vincent i Grenadyny", "Saint Vincent and the Grenadines", "VC", "VCT", "670", "ISO 3166-2:VC", "nonUE"),
        SAINT_BARTHELEMY("Saint-Barthélemy", "Saint Barthélemy", "BL", "BLM", "652", "ISO 3166-2:BL", "nonUE"),
        SAINT_MARTIN("Saint-Martin", "Saint Martin (French part)", "MF", "MAF", "663", "ISO 3166-2:MF", "nonUE"),
        SAINT_PIERRE_I_MIQUELON("Saint-Pierre i Miquelon", "Saint Pierre and Miquelon", "PM", "SPM", "666", "ISO 3166-2:PM", "nonUE"),
        SALWADOR("Salwador", "El Salvador", "SV", "SLV", "222", "ISO 3166-2:SV", "nonUE"),
        SAMOA_AMERYKANSKIE("Samoa Amerykańskie", "American Samoa", "AS", "ASM", "016", "ISO 3166-2:AS", "nonUE"),
        SAMOA("Samoa", "Samoa", "WS", "WSM", "882", "ISO 3166-2:WS", "nonUE"),
        SAN_MARINO("San Marino", "San Marino", "SM", "SMR", "674", "ISO 3166-2:SM", "nonUE"),
        SENEGAL("Senegal", "Senegal", "SN", "SEN", "686", "ISO 3166-2:SN", "nonUE"),
        SERBIA("Serbia", "Serbia", "RS", "SRB", "688", "ISO 3166-2:RS", "nonUE"),
        SESZELE("Seszele", "Seychelles", "SC", "SYC", "690", "ISO 3166-2:SC", "nonUE"),
        SIERRA_LEONE("Sierra Leone", "Sierra Leone", "SL", "SLE", "694", "ISO 3166-2:SL", "nonUE"),
        SINGAPUR("Singapur", "Singapore", "SG", "SGP", "702", "ISO 3166-2:SG", "nonUE"),
        SINT_MAARTEN("Sint Maarten", "Sint Maarten (Dutch part)", "SX", "SXM", "534", "ISO 3166-2:SX", "nonUE"),
        SLOWACJA("Słowacja", "Slovakia", "SK", "SVK", "703", "ISO 3166-2:SK", "UE"),
        SLOWENIA("Słowenia", "Slovenia", "SI", "SVN", "705", "ISO 3166-2:SI", "UE"),
        SOMALIA("Somalia", "Somalia", "SO", "SOM", "706", "ISO 3166-2:SO", "nonUE"),
        SRI_LANKA("Sri Lanka", "Sri Lanka", "LK", "LKA", "144", "ISO 3166-2:LK", "nonUE"),
        STANY_ZJEDNOCZONE("Stany Zjednoczone", "United States", "US", "USA", "840", "ISO 3166-2:US", "nonUE"),
        SUAZI("Suazi", "Swaziland", "SZ", "SWZ", "748", "ISO 3166-2:SZ", "nonUE"),
        SUDAN("Sudan", "Sudan", "SD", "SDN", "736", "ISO 3166-2:SD", "nonUE"),
        SURINAM("Surinam", "Suriname", "SR", "SUR", "740", "ISO 3166-2:SR", "nonUE"),
        SVALBARD_I_JAN_MAYEN("Svalbard i Jan Mayen", "Svalbard and Jan Mayen", "SJ", "SJM", "744", "ISO 3166-2:SJ", "nonUE"),
        SYRIA("Syria", "Syrian Arab Republic", "SY", "SYR", "760", "ISO 3166-2:SY", "nonUE"),
        SZWAJCARIA("Szwajcaria", "Switzerland", "CH", "CHE", "756", "ISO 3166-2:CH", "nonUE"),
        SZWECJA("Szwecja", "Sweden", "SE", "SWE", "752", "ISO 3166-2:SE", "UE"),
        TADZYKISTAN("Tadżykistan", "Tajikistan", "TJ", "TJK", "762", "ISO 3166-2:TJ", "nonUE"),
        TAJLANDIA("Tajlandia", "Thailand", "TH", "THA", "764", "ISO 3166-2:TH", "nonUE"),
        TANZANIA("Tanzania", "Tanzania, United Republic of", "TZ", "TZA", "834", "ISO 3166-2:TZ", "nonUE"),
        TIMOR_WSCHODNI("Timor Wschodni", "Timor-Leste", "TL", "TLS", "626", "ISO 3166-2:TL", "nonUE"),
        TOGO("Togo", "Togo", "TG", "TGO", "768", "ISO 3166-2:TG", "nonUE"),
        TOKELAU("Tokelau", "Tokelau", "TK", "TKL", "772", "ISO 3166-2:TK", "nonUE"),
        TONGA("Tonga", "Tonga", "TO", "TON", "776", "ISO 3166-2:TO", "nonUE"),
        TRYNIDAD_I_TOBAGO("Trynidad i Tobago", "Trinidad and Tobago", "TT", "TTO", "780", "ISO 3166-2:TT", "nonUE"),
        TUNEZJA("Tunezja", "Tunisia", "TN", "TUN", "788", "ISO 3166-2:TN", "nonUE"),
        TURCJA("Turcja", "Turkey", "TR", "TUR", "792", "ISO 3166-2:TR", "nonUE"),
        TURKMENISTAN("Turkmenistan", "Turkmenistan", "TM", "TKM", "795", "ISO 3166-2:TM", "nonUE"),
        TURKS_I_CAICOS("Turks i Caicos", "Turks and Caicos Islands", "TC", "TCA", "796", "ISO 3166-2:TC", "nonUE"),
        TUVALU("Tuvalu", "Tuvalu", "TV", "TUV", "798", "ISO 3166-2:TV", "nonUE"),
        UGANDA("Uganda", "Uganda", "UG", "UGA", "800", "ISO 3166-2:UG", "nonUE"),
        UKRAINA("Ukraina", "Ukraine", "UA", "UKR", "804", "ISO 3166-2:UA", "nonUE"),
        URUGWAJ("Urugwaj", "Uruguay", "UY", "URY", "858", "ISO 3166-2:UY", "nonUE"),
        UZBEKISTAN("Uzbekistan", "Uzbekistan", "UZ", "UZB", "860", "ISO 3166-2:UZ", "nonUE"),
        VANUATU("Vanuatu", "Vanuatu", "VU", "VUT", "548", "ISO 3166-2:VU", "nonUE"),
        WALLIS_I_FUTUNA("Wallis i Futuna", "Wallis and Futuna", "WF", "WLF", "876", "ISO 3166-2:WF", "nonUE"),
        WATYKAN("Watykan", "Holy See (Vatican City State)", "VA", "VAT", "336", "ISO 3166-2:VA", "nonUE"),
        WENEZUELA("Wenezuela", "Venezuela, Bolivarian Republic of", "VE", "VEN", "862", "ISO 3166-2:VE", "nonUE"),
        WEGRY("Węgry", "Hungary", "HU", "HUN", "348", "ISO 3166-2:HU", "UE"),
        WIELKA_BRYTANIA("Wielka Brytania", "United Kingdom", "GB", "GBR", "826", "ISO 3166-2:GB", "nonUE"),
        WIETNAM("Wietnam", "Viet Nam", "VN", "VNM", "704", "ISO 3166-2:VN", "nonUE"),
        WLOCHY("Włochy", "Italy", "IT", "ITA", "380", "ISO 3166-2:IT", "UE"),
        WYBRZEZE_KOSCI_SLONIOWEJ("Wybrzeże Kości Słoniowej", "Côte d’Ivoire", "CI", "CIV", "384", "ISO 3166-2:CI", "nonUE"),
        WYSPA_BOUVETA("Wyspa Bouveta", "Bouvet Island", "BV", "BVT", "074", "ISO 3166-2:BV", "nonUE"),
        WYSPA_BOZEGO_NARODZENIA("Wyspa Bożego Narodzenia", "Christmas Island", "CX", "CXR", "162", "ISO 3166-2:CX", "nonUE"),
        WYSPA_MAN("Wyspa Man", "Isle of Man", "IM", "IMN", "833", "ISO 3166-2:IM", "nonUE"),
        WYSPA_SWIETEJ_HELENY_WYSPA_WNIEBOWSTAPIENIA_I_TRISTAN_DA_CUNHA("Wyspa Świętej Heleny, Wyspa Wniebowstąpienia i Tristan da Cunha", "Saint Helena, Ascension and Tristan da Cunha", "SH", "SHN", "654", "ISO 3166-2:SH", "nonUE"),
        WYSPY_ALANDZKIE("Wyspy Alandzkie", "Åland Islands", "AX", "ALA", "248", "ISO 3166-2:AX", "nonUE"),
        WYSPY_COOKA("Wyspy Cooka", "Cook Islands", "CK", "COK", "184", "ISO 3166-2:CK", "nonUE"),
        WYSPY_DZIEWICZE_STANOW_ZJEDNOCZONYCH("Wyspy Dziewicze Stanów Zjednoczonych", "Virgin Islands, U.S.", "VI", "VIR", "850", "ISO 3166-2:VI", "nonUE"),
        WYSPY_HEARD_I_MCDONALDA("Wyspy Heard i McDonalda", "Heard Island and McDonald Islands", "HM", "HMD", "334", "ISO 3166-2:HM", "nonUE"),
        WYSPY_KOKOSOWE("Wyspy Kokosowe", "Cocos (Keeling) Islands", "CC", "CCK", "166", "ISO 3166-2:CC", "nonUE"),
        WYSPY_MARSHALLA("Wyspy Marshalla", "Marshall Islands", "MH", "MHL", "584", "ISO 3166-2:MH", "nonUE"),
        WYSPY_OWCZE("Wyspy Owcze", "Faroe Islands", "FO", "FRO", "234", "ISO 3166-2:FO", "nonUE"),
        WYSPY_SALOMONA("Wyspy Salomona", "Solomon Islands", "SB", "SLB", "090", "ISO 3166-2:SB", "nonUE"),
        WYSPY_SWIETEGO_TOMASZA_I_KSIAZECA("Wyspy Świętego Tomasza i Książęca", "Sao Tome and Principe", "ST", "STP", "678", "ISO 3166-2:ST", "nonUE"),
        ZAMBIA("Zambia", "Zambia", "ZM", "ZMB", "894", "ISO 3166-2:ZM", "nonUE"),
        ZIMBABWE("Zimbabwe", "Zimbabwe", "ZW", "ZWE", "716", "ISO 3166-2:ZW", "nonUE"),
        ZJEDNOCZONE_EMIRATY_ARABSKIE("Zjednoczone Emiraty Arabskie", "United Arab Emirates", "AE", "ARE", "784", "ISO 3166-2:AE", "nonUE");


        private final String polishName;
        private final String englishName;
        private final String codeAlfa2;
        private final String codeAlfa3;
        private final String codeNumeric;
        private final String codeISO3166_2;
        private final String region;

        public boolean isMatchingByCodeStartsWith(String text, boolean normalize) {
            if (text == null || text.isEmpty()) {
                return false;
            } else {
                text = (normalize) ? text.toUpperCase() : text;
                return codeAlfa2.startsWith(text) || codeAlfa3.startsWith(text) || codeNumeric.startsWith(text) || codeISO3166_2.startsWith(text) || region.startsWith(text);
            }
        }


        public boolean isMatchingByCodeStrict(String text, boolean normalize) {
            if (text == null || text.isEmpty()) {
                return false;
            } else {
                text = (normalize) ? text.toUpperCase() : text;
                return (codeAlfa2.equals(text) || codeAlfa3.equals(text) || codeNumeric.equals(text) || codeISO3166_2.equals(text) || region.equals(text));
            }
        }

        public boolean isMatchingByName(String text) {
            return match(polishName, text) || match(englishName, text);
        }

        @Deprecated
        public boolean matchTo(String text) {
            return match(polishName, text) || match(englishName, text) || match(codeAlfa2, text) || match(codeAlfa3, text) || match(codeNumeric, text) || match(codeISO3166_2, text) || region.equals(text);
        }

        private boolean match(String base, String piece) {
            boolean startWithBigLetter = !piece.isEmpty() && Character.isUpperCase(piece.charAt(0));
            boolean result;
            if (startWithBigLetter) {
                result = base.startsWith(piece) || base.contains(piece);
            } else {
                result = base.toLowerCase().startsWith(piece.toLowerCase()) || base.toLowerCase().contains(piece);
            }
            return result;
        }

        @Override
        public String toString() {
            return "ME:" + super.toString();
        }
    }
}
