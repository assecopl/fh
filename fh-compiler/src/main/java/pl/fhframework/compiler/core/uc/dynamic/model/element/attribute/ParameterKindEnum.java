package pl.fhframework.compiler.core.uc.dynamic.model.element.attribute;

/**
 * Created by pawel.ruta on 2017-03-31.
 */
public enum ParameterKindEnum {
    Input("Input"),
    Output("Output"),
    ;

    private String opis;

    ParameterKindEnum(String opis) {
        this.opis = opis;
    }

    @Override
    public String toString() {
        return opis;
    }
}
