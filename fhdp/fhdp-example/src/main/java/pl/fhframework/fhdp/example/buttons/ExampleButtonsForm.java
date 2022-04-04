package pl.fhframework.fhdp.example.buttons;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Form;

public class ExampleButtonsForm extends Form<ExampleButtonsForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private int intervalText = 0;
        private Integer intervalTime = new Integer(1000);

        public String getInterText() {
            return new Integer(this.intervalText).toString();
        }
    }

}
