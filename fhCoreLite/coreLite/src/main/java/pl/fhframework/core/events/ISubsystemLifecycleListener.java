package pl.fhframework.core.events;

import pl.fhframework.subsystems.Subsystem;

/**
 * Subsystem lifecycle listener
 */
public interface ISubsystemLifecycleListener {

    public void onSubsystemStart(Subsystem subsystem);
}
