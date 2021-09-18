package pl.fhframework;

import pl.fhframework.annotations.ElementPresentedOnTree;
import pl.fhframework.model.dto.SubsystemMetadata;

import java.util.List;

/**
 * Created by Gabriel on 03.07.2016.
 */
@Deprecated
public abstract class PodsystemStatyczny implements Podsystem{
    @Override
    public void dodajPodprzypadki(SubsystemMetadata subsystemMetadata) {
        for (Package pakietPodsystemu : this.getScanedPackages()) {
            List<Class<? extends InicjujacyPrzypadekUzycia>> przypadki = ReflectionUtils.getAnnotatedClasses(pakietPodsystemu.getName(), ElementPresentedOnTree.class, InicjujacyPrzypadekUzycia.class);

        }
    }
}
