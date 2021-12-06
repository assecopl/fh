package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.UseCaseElement;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.Linkable;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Run;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.detail.ActionLink;
import pl.fhframework.compiler.core.uc.dynamic.model.element.detail.UseCaseExit;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "subType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = StartMm.class, name = "start"),
        @JsonSubTypes.Type(value = FinishMm.class, name = "finish"),
        @JsonSubTypes.Type(value = ActionMm.class, name = "action"),
        @JsonSubTypes.Type(value = RunUseCaseMm.class, name = "usecase")
})

@Getter
@Setter
@NoArgsConstructor
public abstract class Element<T extends UseCaseElement> {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected T element;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected UseCaseMm parent;

    public Element(T element, UseCaseMm parent) {
        this.element = element;
        this.parent = parent;
    }

    // todo: restrict elements name - should be unique and remove id?
    @JsonGetter
    public String getId() {
        return element.getId();
    }

    @JsonGetter
    public String getLabel() {
        return element.getLabel();
    }

    @JsonGetter
    public List<Link> getInLinks() {
        return filterLinks(Linkable.class, Linkable::getTargetId).stream().map(link -> Link.getInstance(link, parent, true)).collect(Collectors.toList());
    }

    @JsonGetter
    public List<Link> getOutLinks() {
        return filterLinks(Linkable.class, Linkable::getSourceId).stream().map(link -> Link.getInstance(link, parent, false)).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<ActionLink> getInEventsLinks() {
        return filterLinks(ActionLink.class, ActionLink::getTargetId);
    }

    @JsonIgnore
    public List<RunMm> getInRunLinks() {
        return filterLinks(Run.class, Run::getTargetId).stream().
                map(link -> new RunMm(link, parent, true)).collect(Collectors.toList());
    }

    @JsonIgnore
    public List<UseCaseExit> getInUcExitLinks() {
        return filterLinks(UseCaseExit.class, UseCaseExit::getTargetId);
    }

    protected  <T extends Linkable<?>> List<T> filterLinks(Class<T> type, Function<T, String> side) {
        return parent.getUseCase().getUseCaseCache().getElementIdMapper().values().stream().
                filter(type::isInstance).
                map(type::cast).
                filter(link -> Objects.equals(side.apply(link), element.getId())).collect(Collectors.toList());
    }
}
