package pl.fhframework.dp.commons.fh.parameters.details;

import pl.fhframework.dp.commons.fh.parameters.list.SubstantiveParametersListModel;
import pl.fhframework.model.forms.Form;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SubstantiveParametersDetailForm extends Form<SubstantiveParametersListModel> {

    public List<String> getTags() {
        return Optional.ofNullable(this.getModel().getSelectedSubstantiveParametersDto().getTags())
                .orElse(new ArrayList<>());
    }
}
