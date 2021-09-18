package pl.fhframework.model.forms.provider;

import java.util.List;

/**
 * @author Paweł Domański pawel.domanski@asseco.pl
 */
public interface IComboDataProvider<SRC, RESULT> {
    // bez sygnatury getValues
    default RESULT getValue(SRC element) {return (RESULT) element;}
    public String getDisplayValue(SRC element);

}
