package pl.fhframework.compiler.core.generator.model.usecase;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.fhframework.compiler.core.generator.model.ExpressionMm;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.*;
import pl.fhframework.core.FhException;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "commandType")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ShowFormMm.class, name = "showForm"),
        @JsonSubTypes.Type(value = CallFunctionMm.class, name = "callFunction"),
        @JsonSubTypes.Type(value = RunMm.class, name = "run"),
        @JsonSubTypes.Type(value = ShowMessageMm.class, name = "showMessage")
})
@Getter
@Setter
@NoArgsConstructor
public class CommandMm<T extends Command> implements WithExpression {
    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected UseCaseMm parent;

    @JsonIgnore
    @Getter(AccessLevel.PROTECTED)
    @Setter(AccessLevel.PROTECTED)
    protected T command;

    public CommandMm(T command, UseCaseMm parent) {
        this.command = command;
        this.parent = parent;
    }

    @JsonGetter
    public ExpressionMm getCondition() {
        return new ExpressionMm(command.getCondition());
    }

    @JsonGetter
    public ExpressionMm getReturnHolder() {
        return new ExpressionMm(command.getReturnHolder());
    }

    @JsonGetter
    public boolean isLocalVariable() {
        return command.isLocalVariable();
    }

    @JsonGetter
    public ActivityTypeEnum getActivityType() {
        return command.getActivityType();
    }

    public static CommandMm getInstance(Command command, UseCaseMm parent) {
        if (command instanceof ShowMessage) {
            return new ShowMessageMm((ShowMessage) command, parent);
        }
        if (command instanceof ShowForm) {
            return new ShowFormMm((ShowForm) command, parent);
        }
        if (command instanceof CallFunction) {
            return new CallFunctionMm((CallFunction) command, parent);
        }
        if (command instanceof Run) {
            return new RunMm((Run) command, parent, false);
        }
        throw new FhException(new IllegalArgumentException());
    }

    @Override
    public <T> T provideImpl() {
        return (T) command;
    }
}
