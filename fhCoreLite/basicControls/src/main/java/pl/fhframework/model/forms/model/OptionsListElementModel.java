package pl.fhframework.model.forms.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.function.Predicate;

/**
 * Created by krzysztof.kobylarek on 2017-01-10.
 */
@EqualsAndHashCode(of={"id"})
@Getter @Setter
@AllArgsConstructor
public class OptionsListElementModel {

    public static Predicate<OptionsListElementModel> checkedElements = OptionsListElementModel::isChecked;

    public static Predicate<OptionsListElementModel> notCheckedElements = OptionsListElementModel::isChecked;

    private Integer id;

    private String value;

    private boolean checked = Boolean.FALSE;

    private String icon;

    public OptionsListElementModel(){}

    public OptionsListElementModel(Integer id, String value){
        this.id=id;
        this.value=value;
    }
}