package pl.fhframework;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseNoInput;
import pl.fhframework.core.uc.IUseCaseOutputCallback;
import pl.fhframework.model.dto.UseCaseMetadata;

/**
 * Created by Gabriel on 2015-11-21.
 */
/**
 * Ta klasa powinna zostać rozbita na dwie klasy i interfejs:
 * - kontener InstancjaInicjujacegoPrzypadkuUzycia wykorzystywany przez framework z implementacją rzeczy frameworkowych
 * - interfejs IInicjujacyPrzypadekUzycia, który specyfikuje wymagania frameworku względem konkretnej klasy PU implementowanej przez biznes
 * - klasę InicjujacyPrzypadekUzycia, która abstrahuje od implementacji frameworku i implementuje interfejs wykorzystywany przez framwork
 * Taki sam manewr trzeba zrobić dla podprzypadków.
 *
 * Deprecated - należy używać intefejsów IUseCaseOneInput, IUseCaseNoInput, IUseCaseTwoInput, IInitalUseCase
 */
@Deprecated
public abstract class InicjujacyPrzypadekUzycia extends UseCaseAdapter<Void, IUseCaseNoCallback> implements IInitialUseCase {
    @Getter
    @Setter
    private UseCaseMetadata metadane;

    /**
     * Poprzez tę metodą następuje uruchomienie przypadku użycia.
     * W tej metodzie powinien zostać zainicjowany ewentualny kontekst działania oraz wyświetlona pierwsza form
     */
    //protected abstract void start();

    void uruchom(){
        //this.start();
        //zaktualizujWszystkieKontrolki(false);
    }


//    static void uruchom(MetadataUC useCaseMetadata, ApplicationContext applicationContext, SesjaUzytkownika sesjaUzytkownika) {
//        InicjujacyPrzypadekUzycia przypadekUzycia = null;
//        try {
//            przypadekUzycia = applicationContext.getBean(useCaseMetadata.getKlasa());
//            //przypadekUzycia = useCaseMetadata.getKlasa().newInstance();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//        if (przypadekUzycia != null) {
//            uruchom(przypadekUzycia, sesjaUzytkownika);
//        }
//    }

//    private static void uruchom(InicjujacyPrzypadekUzycia przypadekUzycia, SesjaUzytkownika sesjaUzytkownika) {
//        przypadekUzycia.setUserSession(sesjaUzytkownika);
//        sesjaUzytkownika.setBiezacyPrzypadekUzycia(przypadekUzycia);
//        przypadekUzycia.start();
//        przypadekUzycia.prezentowaneFormatki.forEach(Formatka::updateFormComponents);
//        //przypadekUzycia.aktualizujPrezentowaneFormatki();
//    }
}
