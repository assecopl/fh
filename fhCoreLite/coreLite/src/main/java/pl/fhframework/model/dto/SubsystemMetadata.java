package pl.fhframework.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.InicjujacyPrzypadekUzycia;
import pl.fhframework.annotations.ElementPresentedOnTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Deprecated
@Getter
@Setter
public class SubsystemMetadata {
    private List<UseCaseMetadata> availableUseCases = new ArrayList<>();
    @JsonIgnore
    private Map<String, UseCaseMetadata> idToUseCaseMetadata = new HashMap<>();
    private String label;
    private String id;
    private String image;
    //private boolean something;
    private String package_;
    private int orderNumber;
    private boolean dynamic;


    public UseCaseMetadata addUseCase(UseCaseMetadata useCaseMetadata) {
        availableUseCases.add(useCaseMetadata);
        idToUseCaseMetadata.put(useCaseMetadata.getId(), useCaseMetadata);
        return useCaseMetadata;
    }

    public <T extends InicjujacyPrzypadekUzycia> UseCaseMetadata addUseCase(Class<T> useCaseClass) {
        UseCaseMetadata useCaseMetadata = new UseCaseMetadata();
        ElementPresentedOnTree annotationDataDisplayedOnTreeElement = useCaseClass.getAnnotation(ElementPresentedOnTree.class);

        useCaseMetadata.setId(useCaseClass.getName());
        useCaseMetadata.setLabel(annotationDataDisplayedOnTreeElement.label());
        useCaseMetadata.setClazz(useCaseClass);

        useCaseMetadata.setSpringBean(useCaseClass.isAnnotationPresent(Component.class));
        if (useCaseMetadata.isSpringBean()) {
            Scope scopeAnnotationData = useCaseClass.getAnnotation(Scope.class);
            if (scopeAnnotationData !=null) {
                useCaseMetadata.setSingleton(ConfigurableBeanFactory.SCOPE_SINGLETON.equals(scopeAnnotationData.value()));
                if (useCaseMetadata.isSingleton()) {
                    FhLogger.warn("{} is singleton and parallel access will be blocked(TODO)", useCaseClass.getName());
                }
            }
        }else{
            FhLogger.warn("{} is not Spring bean and has not access to Spring benefits!", useCaseClass.getName());
        }

        return addUseCase(useCaseMetadata);
    }

    public UseCaseMetadata useCaseMetadata(String useCaseId) {
        return idToUseCaseMetadata.get(useCaseId);
    }
}
