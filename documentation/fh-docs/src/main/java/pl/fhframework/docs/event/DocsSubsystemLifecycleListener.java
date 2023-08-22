package pl.fhframework.docs.event;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.compiler.forms.FormsManager;
import pl.fhframework.core.events.ISubsystemLifecycleListener;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.subsystems.Subsystem;

/**
 * Example subsystem lifecycle listener
 */
public class DocsSubsystemLifecycleListener implements ISubsystemLifecycleListener {

    @Autowired
    private FormsManager formsManager;

    @Override
    public void onSubsystemStart(Subsystem subsystem) {
        FhLogger.info(this.getClass(), "Docs started with a form manager injected: " + formsManager.toString());
    }
}
