package pl.fhframework.docs.forms.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import pl.fhframework.core.uc.Parameter;
import pl.fhframework.model.forms.provider.IComboDataProvider;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by pawel.domanski@asseco.pl
 */
@Service
public class DictionaryComboDataProvider implements IComboDataProvider<DictionaryComboDataProvider.SRC, DictionaryComboDataProvider.SRC> {

    static public List<SRC> data = new LinkedList<>(Arrays.asList(
            new DictionaryComboDataProvider.SRC("nowa 1", LocalDate.of(2019,1,4), "value1", "00001","007"),
            new SRC("nowa 2", LocalDate.of(2019,2,4), "value2", "00002","007"),
            new SRC("nowa 3", LocalDate.of(2019,3,4), "value2", "00003","007"),
            new SRC("nowa 4", LocalDate.of(2019,4,4), "value2", "00004","007"),
            new SRC("nowa 5", LocalDate.of(2019,5,4), "value2", "00005","007"),
            new SRC("nowa 6", LocalDate.of(2019,6,4), "value2", "00006","007"),
            new SRC("nowa 7", LocalDate.of(2019,7,4), "value2", "00007","007"),
            new SRC("nowa 8", LocalDate.of(2019,8,4), "value2", "00008","007"),
            new SRC("nowa 9", LocalDate.of(2019,8,4), "value2", "00009","007"),
            new SRC("nowa 10", LocalDate.of(2019,10,4), "value2", "00010","007"),
            new SRC("nowa 11", LocalDate.of(2019,11,4), "value2", "00011","007"),
            new SRC("nowa 12", LocalDate.of(2019,12,4), "value2", "00012","008"),
            new SRC("nowa 13", LocalDate.of(2019,10,4), "value2", "00013","008"),
            new SRC("nowa 14", LocalDate.of(2019,11,4), "value2", "00014","008"),
            new SRC("nowa 15", LocalDate.of(2019,12,4), "value2", "00015","008"),
            new SRC("nowa 16", LocalDate.of(2019,10,4), "value2", "00016","008"),
            new SRC("nowa 17", LocalDate.of(2019,11,4), "value2", "00017","008"),
            new SRC("nowa 18", LocalDate.of(2019,12,4), "value2", "00018","008")
    ));

    static public List<SRC> data2 = new LinkedList<>(Arrays.asList(
            new DictionaryComboDataProvider.SRC("nowa 1", LocalDate.of(2019,1,4), "value1", "00001","007"),
            new SRC("nowa 2", LocalDate.of(2019,2,4), "value2", "00002","007"),
            new SRC("nowa 3", LocalDate.of(2019,3,4), "value2", "00003","007"),
            new SRC("nowa 4", LocalDate.of(2019,4,4), "value2", "00004","007"),
            new SRC("nowa 5", LocalDate.of(2019,5,4), "value2", "00005","007"),
            new SRC("nowa 6", LocalDate.of(2019,6,4), "value2", "00006","007"),
            new SRC("nowa 7", LocalDate.of(2019,7,4), "value2", "00007","007"),
            new SRC("nowa 8", LocalDate.of(2019,8,4), "value2", "00008","007"),
            new SRC("nowa 9", LocalDate.of(2019,8,4), "value2", "00009","007"),
            new SRC("nowa 10", LocalDate.of(2019,10,4), "value2", "00010","007"),
            new SRC("nowa 11", LocalDate.of(2019,11,4), "value2", "00011","007"),
            new SRC("nowa 12", LocalDate.of(2019,12,4), "value2", "00012","008"),
            new SRC("nowa 13", LocalDate.of(2019,10,4), "value2", "00013","008"),
            new SRC("nowa 14", LocalDate.of(2019,11,4), "value2", "00014","008"),
            new SRC("nowa 15", LocalDate.of(2019,12,4), "value2", "00015","008"),
            new SRC("nowa 16", LocalDate.of(2019,10,4), "value2", "00016","008"),
            new SRC("nowa 17", LocalDate.of(2019,11,4), "value2", "00017","008"),
            new SRC("nowa 18", LocalDate.of(2019,12,4), "value2", "00018","008")
    ));

    @Override
    public SRC getCode(SRC element) {
        return element;
    }

    @Override
    public String getDisplayValue(SRC element) {
        return element.name;
    }

    @Override
    public Boolean areObjectsEquals(SRC element1, SRC element2) {
        return Objects.equals(element1, element2);
    }

    public SRC getValue(SRC code,  @Parameter(name = "codeListId") String codeListId, @Parameter(name = "referenceDate") LocalDate referenceDate) {
        if(code == null) {
            return null;
        }
        Optional<SRC> r = data2.stream().filter(e -> (e.code.equals(code.code) && e.codeListId.equals(codeListId))).findFirst();
        if(r.isPresent()){
            return r.get();
        }
        return null;
    }

    public List<SRC> getValues(String text, @Parameter(name = "codeListId") String codeListId, @Parameter(name = "referenceDate") LocalDate referenceDate) {
        if (text == null || Objects.equals(text, "")) {
            return data.stream().filter(e -> (e.codeListId.equals(codeListId))).collect(Collectors.toList());
        } else {
            return data.stream().filter(e -> (e.name.contains(text) && e.codeListId.equals(codeListId))).collect(Collectors.toList());
        }
    }


    @AllArgsConstructor
    public static class SRC {
        String name;
        LocalDate referenceDate;
        String value;
        String code;
        String codeListId;
    }


}



