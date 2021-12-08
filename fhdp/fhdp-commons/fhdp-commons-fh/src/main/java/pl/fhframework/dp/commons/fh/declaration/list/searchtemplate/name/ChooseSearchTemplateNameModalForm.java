package pl.fhframework.dp.commons.fh.declaration.list.searchtemplate.name;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDto;
import pl.fhframework.model.forms.Form;

public class ChooseSearchTemplateNameModalForm extends Form<ChooseSearchTemplateNameModalForm.Model> {
    @Getter
    @Setter
    public static class Model {
        private SearchTemplateDto pkwdSearchTemplateDto;
        private String templateName;
    }
}
