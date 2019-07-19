package pl.fhframework.model.dto.cloud;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import pl.fhframework.model.forms.AdHocForm;
import pl.fhframework.model.forms.Form;
import pl.fhframework.model.forms.FormState;
import pl.fhframework.model.forms.attribute.FormType;

import java.time.Instant;

/**
 * Information about form running on a cloud server.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CloudFormInfo {

    private String id;

    private String container;

    private FormType type;

    private FormState state;

    private long showingTimestampSecond;

    private int showingTimestampNano;

    public static CloudFormInfo toFormInfo(Form<?> form) {
        return new CloudFormInfo(form.getId(), form.getContainer(), form.getEffectiveFormType(), form.getState(),
                form.getShowingTimestamp().getEpochSecond(),
                form.getShowingTimestamp().getNano());
    }

    public static Form<?> toArtificialForm(CloudFormInfo formInfo) {
        AdHocForm<?> form = new AdHocForm<>();
        form.setId(formInfo.getId());
        form.setDeclaredContainer(formInfo.getContainer());
        form.setFormType(formInfo.getType());
        form.setState(formInfo.getState());
        form.setShowingTimestamp(Instant.ofEpochSecond(formInfo.getShowingTimestampSecond(), formInfo.getShowingTimestampNano()));
        return form;
    }
}
