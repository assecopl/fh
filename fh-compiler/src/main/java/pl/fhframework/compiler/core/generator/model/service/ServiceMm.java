package pl.fhframework.compiler.core.generator.model.service;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.ModuleMetaModel;
import pl.fhframework.compiler.core.generator.model.MetaModel;
import pl.fhframework.compiler.core.services.dynamic.model.Service;
import pl.fhframework.modules.services.ServiceTypeEnum;

import java.util.List;
import java.util.stream.Collectors;

// todo:
@Getter
@Setter
@NoArgsConstructor
public class ServiceMm extends MetaModel {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private Service service;

    public ServiceMm(Service service, List<String> dependencies, ModuleMetaModel metadata) {
        setDependencies(dependencies);
        this.service = service;
    }

    @JsonGetter
    public String getId() {
        return service.getId();
    }

    @JsonGetter
    public ServiceTypeEnum getServiceType() {
        return getService().getServiceType();
    }

    @JsonGetter
    public String getEndpointName() {
        return getService().getEndpointName();
    }

    @JsonGetter
    public String getEndpointUrl() {
        return getService().getEndpointUrl();
    }

    @JsonGetter
    public String getBaseUri() {
        return getService().getBaseUri();
    }

    @JsonGetter
    public List<OperationMm> getOperations() {
        return service.getOperations().stream().map(OperationMm::new).collect(Collectors.toList());
    }

    @Override
    public <T> T provideImpl() {
        return (T) service;
    }
}
