package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.UseCaseElement;
import pl.fhframework.core.uc.meta.UseCaseInfo;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
public class UseCaseInfoMm extends ElementInfo {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCaseInfo useCaseInfo;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    private UseCaseElement element;

    public UseCaseInfoMm(UseCaseInfo useCaseInfo, UseCaseElement element) {
        this.useCaseInfo = useCaseInfo;
        this.element = element;
    }

    @JsonGetter
    public String getId() {
        return element.getId();
    }

    @JsonGetter
    public String getUsecaseId() {
        return useCaseInfo.getId();
    }

    @JsonGetter
    public ActionInfo getStart() {
        return new StartInfo(useCaseInfo.getStart());
    }

    @JsonGetter
    public List<ActionInfo> getExits() {
        return useCaseInfo.getExits().stream().map(FinishInfo::new).collect(Collectors.toList());
    }
}
