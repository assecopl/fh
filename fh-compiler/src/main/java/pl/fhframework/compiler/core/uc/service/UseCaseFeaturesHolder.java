package pl.fhframework.compiler.core.uc.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import pl.fhframework.compiler.forms.DynamicFormMetadata;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.compiler.core.dynamic.IDynamicClassResolver;
import pl.fhframework.compiler.core.dynamic.RuntimeErrorDescription;
import pl.fhframework.compiler.core.uc.dynamic.model.UseCase;
import pl.fhframework.core.uc.meta.UseCaseInfo;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Created by pawel.ruta on 2017-06-28.
 */
@Data
@AllArgsConstructor
public class UseCaseFeaturesHolder {
    private UseCase useCase;

    private Map<String, UseCaseInfo> useCasesInfo;

    private Map<String, DynamicFormMetadata> formsInfoMap;

    // <from name, <action name, action info>>
    private Map<String, Map<String, ActionSignature>> eventsInfoMap;

    private IDynamicClassResolver dynamicClassRepository;

    private Set<String> validatedUseCases;

    private List<RuntimeErrorDescription> errorsDescriptions;

    public Optional<ActionSignature> getEventInfo(String form, String eventName) {
        Map<String, ActionSignature> actionsMap = getEventsInfoMap().get(form);

        if (actionsMap != null) {
            return Optional.ofNullable(actionsMap.get(eventName));
        }

        return Optional.empty();
    }
}
