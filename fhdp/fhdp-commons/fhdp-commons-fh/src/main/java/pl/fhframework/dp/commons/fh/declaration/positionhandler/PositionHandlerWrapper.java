package pl.fhframework.dp.commons.fh.declaration.positionhandler;

import lombok.Builder;
import org.apache.commons.lang3.SerializationUtils;
import pl.fhframework.dp.commons.fh.declaration.handling.BaseDeclarationFormModel;
import pl.fhframework.dp.commons.fh.utils.FhUtils;
import pl.fhframework.dp.commons.fh.utils.objects.init.PojoInitializer;
import pl.fhframework.dp.commons.fh.utils.objects.init.PojoInitializerConfig;
import pl.fhframework.dp.transport.enums.RowStateEnum;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;

import java.io.Serializable;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class implements basic operations (add, edit, delete) for a given type of object in the table
 * at the forms from generator. Creation and configuration is supported by the builder
 *
 * @param <POSITION> is the type of position in the table
 * @param <MODEL>    is the model that holds table
 */
@Builder
public class PositionHandlerWrapper<POSITION extends Serializable, MODEL extends BaseDeclarationFormModel> implements IPositionHandlerBase<MODEL, POSITION> {

    private final Consumer<POSITION> modelSelectedPositionSetter;  //pass setter to selected position
    private final Supplier<POSITION> modelSelectedPositionGetter; //pass getter to selected position
    private final Consumer<POSITION> PositionFromListRemover; //pass operation for removal an element from the list
    private final Consumer<POSITION> PositionFromListEditor; //pass operation for edit an element from the list
    private final Consumer<Object> PositionParentSelector;
    private final Consumer<POSITION> PositionToListAdder; //pass operation for adding an element to the list
    private final Supplier<String> variantResolver; //pass method variant resolver, typically from AbstractDeclarationHandler
    private final String formWithTableReference; //pass full qualified path to frm that displays table
    private final Class<POSITION> positionClass; //position class
    private final Class elementClass; //element class
    private final Class<? extends TablePositionBaseUC<MODEL>> displayEditFormUseCaseImpl; //UC class that displays popup with row details
    private final TriConsumer<MODEL, String, String> refreshMethod; //pass refresh method after popup is closed, typically initWniosekRightPanel
    private final Consumer<Boolean> isEditMode;
    private final Supplier<POSITION> initializer;
    private final Consumer<MODEL> modelSelectedSequenceNumberSetter;
    private final Consumer<String> modelSelectedRowStateSetter;  //pass setter to rowState of selected position
    private final Supplier<String> modelSelectedRowStateGetter;  //pass getter to rowState of selected position

    @Builder.Default
    private final String confirmDeleteMessage = "Do you really want to delete selected position?"; //if needed customize the message that displays when user does record delete

    public void onAddItem(IUseCase useCase, MODEL model) {
        setIsEditMode(true);

        modelSelectedPositionSetter.accept(getNewItem());

        if(modelSelectedSequenceNumberSetter != null) {
            modelSelectedSequenceNumberSetter.accept(model);
        }
        useCase.runUseCase(displayEditFormUseCaseImpl, model, new IUseCaseSaveCancelCallback<MODEL>() {
            @Override
            public void save(MODEL one) {
                PositionToListAdder.accept(modelSelectedPositionGetter.get());

                if(variantResolver.get().equals("Correction")) {
                    modelSelectedRowStateSetter.accept(RowStateEnum.A.name());
                }

                refreshMethod.accept(model, formWithTableReference, variantResolver.get());
                setIsEditMode(false);
            }

            @Override
            public void cancel() {
                setIsEditMode(false);
            }
        });
    }

    public void onEmptyItem(MODEL model) {
        modelSelectedPositionSetter.accept(getNewItem());
        PositionToListAdder.accept(modelSelectedPositionGetter.get());
        refreshMethod.accept(model, formWithTableReference, variantResolver.get());
        setIsEditMode(false);
    }

    public void onEditItem(IUseCase useCase, MODEL model) {
        POSITION initialPosition = modelSelectedPositionGetter.get();
        if (initialPosition == null) {
            return;
        }
        modelSelectedPositionSetter.accept(copyItem(initialPosition)); //new object for edit, original content is in  jst w initialPosition
        useCase.runUseCase(displayEditFormUseCaseImpl, model, new IUseCaseSaveCancelCallback<MODEL>() {
            @Override
            public void save(MODEL one) {
                PositionFromListEditor.accept(SerializationUtils.clone(modelSelectedPositionGetter.get()));
            }

            @Override
            public void cancel() {
                modelSelectedPositionSetter.accept(initialPosition);
            }
        });
    }

    public void onDeleteItem() {
        FhUtils.showConfirmDialogYesNo("Potwierdzenie", confirmDeleteMessage, () -> {
            POSITION POSITION = modelSelectedPositionGetter.get();
            PositionFromListRemover.accept(POSITION);
            modelSelectedPositionSetter.accept(null);
        });
    }

    public void onDeleteItem(POSITION POSITION) {
        FhUtils.showConfirmDialogYesNo("Potwierdzenie", confirmDeleteMessage, () -> {
            PositionFromListRemover.accept(POSITION);
        });
    }

    public void onDeleteItem(POSITION POSITION, String confirmation, String confirmationDeleteMessage) {
        FhUtils.showConfirmDialogYesNo(confirmation, confirmationDeleteMessage, () -> {
            PositionFromListRemover.accept(POSITION);
        });
    }

    public void onDeleteItem(POSITION POSITION, String confirmation, String confirmationDeleteMessage,
                             String labelButtonYes, String labelButtonNo) {
        FhUtils.showConfirmDialogYesNo(confirmation, confirmationDeleteMessage, labelButtonYes, () -> {

            if(variantResolver.get().equals("Correction")) {
                modelSelectedPositionSetter.accept(POSITION);
                if(RowStateEnum.A.name().equals(modelSelectedRowStateGetter.get())) {
                    // if we are in correction mode and row was added now
                    // then we delete this position
                    PositionFromListRemover.accept(POSITION);
                } else {
                    // if we are in correction mode and row was not added now
                    // then we only mark position (row) for delete
                    modelSelectedRowStateSetter.accept(RowStateEnum.D.name());
                }
            } else {
                PositionFromListRemover.accept(POSITION);
            }

        }, labelButtonNo, () -> {});
    }

    public void onSelectedItem(Object parent) {
        PositionParentSelector.accept(parent);
    }

    public void setSelectedItem(POSITION POSITION) { modelSelectedPositionSetter.accept(POSITION); }

    private POSITION copyItem(POSITION src) {
        return SerializationUtils.clone(src);
    }

    private POSITION getNewItem() {
        try {
            if(initializer != null) {
                return initializer.get();
            } else {
                Object element = elementClass.getDeclaredConstructor().newInstance();
                Object oldElement = elementClass.getDeclaredConstructor().newInstance();

                PojoInitializer.initializePojo(element, new PojoInitializerConfig());
                PojoInitializer.initializePojo(oldElement, new PojoInitializerConfig());

                POSITION o = positionClass.getDeclaredConstructor(
                        Object.class,
                        Object.class
                ).newInstance(
                        element,
                        oldElement
                );
                return o;
            }
        } catch (Exception e) {
            FhLogger.error(e);
            throw new RuntimeException(e.getMessage());
        }
    }

    public void setIsEditMode(boolean editMode){
        if (isEditMode != null){
            isEditMode.accept(editMode);
        }
    }

}
