package pl.fhframework.core.session.scope;

import lombok.Getter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for beans of user session (not http session).
 */
@Getter
public class SessionScopeBeanContainer {

    private Map<String, Object> scopedObjects = new HashMap<>();

    private Map<String, Runnable> destructionCallbacks = new HashMap<>();
}
