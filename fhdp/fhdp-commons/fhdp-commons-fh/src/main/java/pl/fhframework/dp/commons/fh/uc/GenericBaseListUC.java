package pl.fhframework.dp.commons.fh.uc;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.model.IParamVariant;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.fhPersistence.anotation.Approve;
import pl.fhframework.fhPersistence.anotation.Cancel;
import pl.fhframework.fhPersistence.services.ChangesService;
import pl.fhframework.model.forms.Form;

/**
 * Generic list use case. Template classes:
 * D: internal list data model
 * Fp: Detail form parameters model
 *
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-06
 */
@UseCase
public abstract class GenericBaseListUC<LIST, PARAMS, RESULT> extends FhdpBaseUC {

    @Getter
    protected LIST listData;

    @Getter
    protected Form form;

    @Autowired
    protected ChangesService changesService;


    protected void init() {
        listData = initInternalListData();
        String listFormId = getListFormId();
        if(isImmediate()) {
            readData();
        }
//        Class<Form> formClass = (Class<Form>) dynamicClassRepository.getOrCompileDynamicClass(DynamicClassName.forClassName(listFormId));
        form = showFormX(listFormId, listData);
    }

    protected abstract Form showFormX(String listFormId, LIST listData);

    protected boolean isImmediate() {
        return true;
    };

    protected abstract void readData();

    /**
     * Metoda ma zwrócić kwalifikowaną nazwę klasy formularza listy
     * np. com.skg.vls.vlsi.module.list.ServiceDefinitionsList
     *
     * @return
     */
    protected abstract String getListFormId();

    /**
     * Metoda zwraca zainicjowaną klasę modelu listy
     * W najprostszym przypadku nową instancję
     *
     * @return
     */
    protected abstract LIST initInternalListData();

    /**
     * Metoda zwraca instancję parametrów dla formularza edycji przygotowaną pod nowy rekord.
     * @return
     */
    protected abstract PARAMS getParamsForNew();

    /**
     * Metoda zwraca instancję parametrów dla formularza edycji przygotowaną w trybie edycji.
     * @return
     */
    protected abstract PARAMS getParamsForEdit();

    /**
     * Metoda zwraca klasę formularza edycji
     * @return
     */
    protected abstract Class getEditFormClass();

    /**
     * Metoda zwraca domyślny callback - może zostać nadpisana, jeżeli któraś z metod musi robić coś więcej.
     * @param isNew
     * @return
     */
    protected IGenericListOutputCallback getEditListOutputCallback(boolean isNew) {
        return new EditListOutputCallback(isNew);
    }

    /**
     * Metoda, w której możemy coś wykonać przed zamknięciem listy
     */
    private void preClose() {

    }


    @Action("close")
    @Cancel
    public void close() {
        preClose();
        exitX();
    }

    protected abstract void exitX();

    @Action("search")
    public void search() {
        changesService.clearContext();
        readData();
    }



    @Action("newItem")
    public void newDefinition() {
        PARAMS params = getParamsForNew();
        openEditForm(params, true);
    }

    private void openEditForm(PARAMS params, boolean isNew) {
        runUseCaseX(getEditFormClass(), params, getEditListOutputCallback(isNew));
    }

    protected abstract void runUseCaseX(Class editFormClass, PARAMS params, IGenericListOutputCallback editListOutputCallback);

    @Action("edit")
    public void edit() throws CloneNotSupportedException {
        PARAMS params = getParamsForEdit();
        openEditForm(params, false);
    }

    /**
     * Należy wydziedziczyć jako akcję, która czyści obiekt query
     */
    public abstract void clearQuery();

    @Action("view")
    public void view() {
        PARAMS params = getParamsForEdit();
        if(params instanceof IParamVariant) {
            ((IParamVariant)params).setFormVariant("VIEW");
        }
        openEditForm(params, false);
    }

    private class EditListOutputCallback implements IGenericListOutputCallback<RESULT> {

        private final boolean isNew;

        public EditListOutputCallback(boolean isNew) {
            this.isNew = isNew;
        }

        @Override
        public void delete() {
            readData();
        }


        @Override
        public void cancel() {}

        @Override
        @Approve
        public void save(RESULT result) {
            readData();
        }
    }
}
