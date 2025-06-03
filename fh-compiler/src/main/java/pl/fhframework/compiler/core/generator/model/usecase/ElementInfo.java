package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "elementType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActionInfo.class, name = "action"),
        @JsonSubTypes.Type(value = StartInfo.class, name = "start"),
        @JsonSubTypes.Type(value = FinishInfo.class, name = "finish"),
        @JsonSubTypes.Type(value = UseCaseInfoMm.class, name = "usecase")
})
@Getter
@Setter
@NoArgsConstructor
public abstract class ElementInfo {
    public abstract String getId();
}
