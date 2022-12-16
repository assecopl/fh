package pl.fhframework.dp.commons.fh.parameters.details;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersListModel;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubstantiveParametersDetailForm extends Form<SubstantiveParametersListModel> {
    @Autowired
    private MessageService messageService;

    public List<String> getTags() {
        return Optional.ofNullable(this.getModel().getSelectedSubstantiveParametersDto().getTags())
                .orElse(new ArrayList<>());
    }

    public String getSelectedSelectedSubstantiveParameterTranslatedDescription(){
        String notTranslated = this.getModel().getSelectedSubstantiveParametersDto().getParameterDescriptions().get(0).getDescription();
        if (notTranslated.startsWith("$.")) {
            String translated = this.messageService.getAllBundles().getMessage(notTranslated.substring(2));
            return translated;
        }else{
            return notTranslated;
        }
    }
}
