package pl.fhframework;

import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.model.dto.SubsystemMetadata;
import pl.fhframework.model.dto.UseCaseMetadata;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Gabriel on 03.07.2016.
 */
@Deprecated
public class PodsystemAdHoc implements Podsystem {
    private String sciezka;

    public PodsystemAdHoc(String sciezka) {
        this.sciezka = sciezka;
    }

    public PodsystemAdHoc(Class<PodsystemAdHoc> klasaPodsystemu) {
        this.sciezka = sciezkaBazowa(klasaPodsystemu);
    }

    @Override
    public String getLabel() {
        return "DYNAMICZNY";
    }

    @Override
    public String getNazwa() {
        return "Dynamiczny";
    }

    @Override
    public String getDescription() {
        return "Podsystem Dynamiczny";
    }

    @Override
    public String getImage() {
        return "user";
    }

    @Override
    public int getPositionNumber() {
        return 10;
    }

    @Override
    public Package[] getScanedPackages() {
        return new Package[0];
    }

    @Override
    public void dodajPodprzypadki(SubsystemMetadata subsystemMetadata) {
        List<UseCaseMetadata> metadaneDynamicznychPU = dajMetadanePrzypadkowUzycia();
        for (UseCaseMetadata useCaseMetadata : metadaneDynamicznychPU) {
            subsystemMetadata.addUseCase(useCaseMetadata);
        }
    }

    private List<UseCaseMetadata> dajMetadanePrzypadkowUzycia() {
        List<UseCaseMetadata> zwracanaLista = new ArrayList<>();
        try {
            Files.walk(Paths.get(sciezka)).filter(Files::isRegularFile).forEach(path -> {
                String nazwa = getNazwa();
                String label = getNazwa();
                UseCaseMetadata useCaseMetadata = new UseCaseMetadata();
                useCaseMetadata.setLabel(label);
                useCaseMetadata.setSpringBean(false);
                useCaseMetadata.setSingleton(true);
                useCaseMetadata.setId(nazwa);
                    }
            );
        } catch (IOException e) {
            FhLogger.error("Problem przy wczytywaniu plików opisujących przydadki użycia w '{}'!", this.getClass().getName(), e);
        }
        return zwracanaLista;
    }

    private String sciezkaBazowa(Class<PodsystemAdHoc> podsystemAdHocClass) {
        String lokacjaAplikacji = podsystemAdHocClass.getProtectionDomain().getCodeSource().getLocation().getPath();
        String prefix = "";
        if (lokacjaAplikacji.startsWith("file")) {

        } else {
            prefix = Paths.get(lokacjaAplikacji.substring(1)).toAbsolutePath().toFile().getParentFile().getParent();
        }
        return prefix;
    }
}
