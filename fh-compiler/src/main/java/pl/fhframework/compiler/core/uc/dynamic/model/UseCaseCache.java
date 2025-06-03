package pl.fhframework.compiler.core.uc.dynamic.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Finish;
import pl.fhframework.compiler.core.uc.dynamic.model.element.Start;
import pl.fhframework.compiler.core.uc.dynamic.model.element.behaviour.WithParameters;
import pl.fhframework.compiler.core.uc.dynamic.model.element.command.Command;
import pl.fhframework.compiler.core.uc.dynamic.model.element.repository.Function;
import pl.fhframework.core.uc.dynamic.model.element.behaviour.Identifiable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by pawel.ruta on 2017-03-29.
 */
@Getter
@Setter
public class UseCaseCache {
    @Setter(AccessLevel.PRIVATE)
    // <Id of element, Element>
    private Map<String, Identifiable> elementIdMapper = new HashMap<>();

    private Map<String, Identifiable> functionIdMapper = new HashMap<>();

    private Start start;

    private List<Finish> exits = new LinkedList<>();

    private List<Command> commands = new LinkedList<>();

    public void addElement(Identifiable element) {
        elementIdMapper.put(element.getId(), element);
        if (element instanceof Start) {
            start = (Start) element;
        }
        else if (element instanceof Finish) {
            exits.add((Finish) element);
        }
    }

    public void removeElement(Identifiable element) {
        elementIdMapper.remove(element.getId());
        if (element instanceof Start) {
            start = null;
        }
        else if (element instanceof Finish) {
            exits.remove((Finish) element);
        }
    }

    public Identifiable getElement(String id) {
        if (id == null) {
            return null;
        }

        return elementIdMapper.get(id);
    }

    public void addCommand(Command command) {
        commands.add(command);
    }

    public void removeCommand(Command command) {
        commands.remove(command);
    }

    public void addFunction(Function function) {
        functionIdMapper.put(function.getId(), function);
    }

    public void removeFunction(Function function) {
        functionIdMapper.remove(function.getId());
    }

    public void getFunction(String id) {
        functionIdMapper.get(id);
    }

    public List<WithParameters> getElementsWithParams() {
        List<WithParameters> withParameters = new LinkedList<>();
        withParameters.addAll(elementIdMapper.values().stream().filter(WithParameters.class::isInstance).
                map(WithParameters.class::cast).collect(Collectors.toList()));
        withParameters.addAll(commands.stream().filter(WithParameters.class::isInstance).
                map(WithParameters.class::cast).collect(Collectors.toList()));

        return withParameters;
    }

    public List<Identifiable> values() {
        return new LinkedList<>(elementIdMapper.values());
    }

    public String getStartId() {
        if (start != null) {
            return start.getId();
        }
        return null;
    }
}
