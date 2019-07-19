package pl.fhframework;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.annotations.ElementPresentedOnTree;
import pl.fhframework.model.dto.SubsystemMetadata;

import java.util.List;

/**
 * Created by Gabriel on 04.03.2016.
 */
@Deprecated
public interface Podsystem {
    String getLabel();
    String getNazwa();
    String getDescription();
    String getImage();
    int getPositionNumber();
    default Package[] getScanedPackages(){
        return new Package[]{this.getClass().getPackage()};
    }

    default void dodajPodprzypadki(SubsystemMetadata subsystemMetadata){
        Class<?> klasaPodsystemu = this.getClass();
        for (Package pakietPodsystemu : this.getScanedPackages()) {
            List<Class<? extends InicjujacyPrzypadekUzycia>> klasyPrzypadkow = ReflectionUtils.getAnnotatedClasses(pakietPodsystemu.getName(), ElementPresentedOnTree.class, InicjujacyPrzypadekUzycia.class);
            for (Class<? extends InicjujacyPrzypadekUzycia> klasaPU : klasyPrzypadkow){
                try {
                    ElementPresentedOnTree metadanePU =  klasaPU.getAnnotation(ElementPresentedOnTree.class);
                    if (klasaPodsystemu.equals(metadanePU.group())) {
                        subsystemMetadata.addUseCase(klasaPU);
                        FhLogger.info(this.getClass(), "Wczytywanie PU: {} jako '{}'", klasaPU.getName(), subsystemMetadata.getLabel());
                    }else{
                        FhLogger.info(this.getClass(), "Pominięcie PU: {}", klasaPU.getName());
                    }
                } catch (Exception e) {
                    FhLogger.error("Problem przy wczytywaniu PU: {}", klasaPU.getName(), e);
                }
            }
        }
    }

}
