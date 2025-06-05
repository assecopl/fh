package pl.fhframework.docs.forms.component.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pl.fhframework.core.designer.ComponentElement;

@Getter
@Setter
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class RadioOptionElement extends ComponentElement {
    private String radioNigeria = "Nigeria";
    private String radioSenegal = "Senegal";
    private String radioGhana = "Ghana";
    private String radioZimbabwe = "Zimbabwe";
    private String selectCountry = "";
    private String selectCountry2 = "";

    private RadioEnum selectedEnum;

    public enum RadioEnum {
        Nigeria("Radio Nigeria"),
        Senegal("Radio Senegal"),
        ;

        private String opis;

        RadioEnum(String opis) {
            this.opis = opis;
        }

        @Override
        public String toString() {
            return opis;
        }
    }
}
