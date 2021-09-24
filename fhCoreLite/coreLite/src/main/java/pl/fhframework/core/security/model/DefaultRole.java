package pl.fhframework.core.security.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import pl.fhframework.core.security.IDefaultRole;
import pl.fhframework.core.security.IFunction;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Default role model use for initialize system by custom roles if there are no users and roles in the repository.
 * @author tomasz.kozlowski (created on 2018-05-22)
 */
@Getter
@EqualsAndHashCode(of = "name")
public class DefaultRole implements IDefaultRole {

    private String name;
    private Set<IFunction> functions;
    private boolean allFunctions;

    public DefaultRole(String name, IFunction... functions) {
        this.name = name;
        this.functions = Arrays.stream(functions).collect(Collectors.toSet());
        this.allFunctions = false;
    }

    public DefaultRole(String name) {
        this.name = name;
        this.allFunctions = true;
    }

}
