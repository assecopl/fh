package pl.fhframework.core.uc.url;

import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Use case URL
 */
public class UseCaseUrl {
    public static final String REMOTE_EVENT = "REMOTE_EVENT";
    public static final String REMOTE_EVENT_NAME = "remoteEvent";

    private static final String POSITIONAL_PREFIX = "__POSITIONAL__";

    @Getter
    @Setter
    private String useCaseAlias;

    @Getter
    private Map<String, String> parameters = new HashMap<>();

    @Getter
    private int maxPositionalIndex = -1;

    @Getter
    @Setter
    private String url;

    public void putPositionalParameter(int index, String value) {
        parameters.put(POSITIONAL_PREFIX + index, value);
        maxPositionalIndex = Math.max(index, maxPositionalIndex);
    }

    public void putNamedParameter(String name, String value) {
        parameters.put(name, value);
    }

    public String getPositionalParameter(int index) {
        return parameters.get(POSITIONAL_PREFIX + index);
    }

    public String getNamedParameter(String name) {
        return parameters.get(name);
    }

    public Set<String> getParameterNames() {
        return parameters.keySet().stream().filter(k -> !k.startsWith(POSITIONAL_PREFIX)).collect(Collectors.toSet());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UseCaseUrl that = (UseCaseUrl) o;

        if (!useCaseAlias.equals(that.useCaseAlias)) return false;
        if (!parameters.equals(that.parameters)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return useCaseAlias.hashCode();
    }

    @Override
    public String toString() {
        return useCaseAlias + ": " + parameters.toString();
    }
}
