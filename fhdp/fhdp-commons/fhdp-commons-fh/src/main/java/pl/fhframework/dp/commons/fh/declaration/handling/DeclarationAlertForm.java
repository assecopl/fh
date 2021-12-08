package pl.fhframework.dp.commons.fh.declaration.handling;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import pl.fhframework.dp.transport.dto.alerts.AlertDto;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.PageModel;

import java.util.List;

public class DeclarationAlertForm extends Form<DeclarationAlertForm.Model> {

    public DeclarationAlertForm() {}

    @Getter
    @Setter
    public static class Model {
        private PageModel<AlertDto> declarationAlertResult;

        public String getRoleList(List<String> roles) {
            return StringUtils.join(roles, ", ");
        }
    }
}
