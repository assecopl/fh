package pl.fhframework.subsystems;

import pl.fhframework.core.io.FhResource;

/**
 * An artificial subsystem used to maintain base path and any name obtained from non-subsystem module (e.g. external jars).
 */
public class ArtificialSubsystem extends Subsystem {

    public ArtificialSubsystem(String name, FhResource baseUrl) {
        super(name, name, baseUrl, null, baseUrl, "");
    }
}
