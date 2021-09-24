package pl.fhframework.trees;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

/**
 * Created by pawel.ruta on 2018-09-07.
 */
@Service
public class SubsystemElementsTreeListener implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private SubsystemElementsTree subsystemElementsTree;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        subsystemElementsTree.onApplicationEvent(event);
    }
}
