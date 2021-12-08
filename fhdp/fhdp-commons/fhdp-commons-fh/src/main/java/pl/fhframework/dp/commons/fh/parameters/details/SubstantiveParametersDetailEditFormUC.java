package pl.fhframework.dp.commons.fh.parameters.details;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.dp.commons.fh.services.SubstantiveParametersTagService;
import pl.fhframework.dp.transport.cdm.types.ValidationResultTypeST;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersParameterItem;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDtoQuery;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.uc.IUseCaseOneInput;
import pl.fhframework.core.uc.IUseCaseSaveCancelCallback;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.event.EventRegistry;
import pl.fhframework.model.PresentationStyleEnum;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UseCase
public class SubstantiveParametersDetailEditFormUC implements IUseCaseOneInput<SubstantiveParametersDetailEditForm.Model, IUseCaseSaveCancelCallback<SubstantiveParametersDetailEditForm.Model>> {

    @Autowired
    private MessageService messageService;

    @Autowired
    private EventRegistry eventRegistry;

    @Autowired
    private SubstantiveParametersTagService substantiveParametersTagService;

    private SubstantiveParametersDetailEditForm.Model model;

    private SubstantiveParametersTagDtoQuery query;

    @Override
    public void start(SubstantiveParametersDetailEditForm.Model one) {
        this.model = one;

        // handles collection of parameter's tags
        query = new SubstantiveParametersTagDtoQuery();
        query.setSortProperty("name.keyword");
        this.model.setSubstantiveParametersTagList(Optional.ofNullable(substantiveParametersTagService.listDto(query)).map(item -> item.stream().map(SubstantiveParametersTagDto::getName)).orElseGet(Stream::empty).collect(Collectors.toList()));

        showForm("pl.fhframework.dp.commons.fh.parameters.details.SubstantiveParametersDetailEditForm", model);

        eventRegistry.fireFocusEvent(getActiveForm().getContainer(), "addTag");
    }

    @Action(validate = true)
    public void save() {
        model.getDto().setTagsDisplay(
                model.getDto().getTags().stream().map(String::valueOf).collect(Collectors.joining(","))
        );
        this.exit().save(model);
    }

    @Action(validate = false)
    public void cancel() {
        this.exit().cancel();
    }

    @Action(validate = false)
    public void addRepeater() {
        model.getDto().getValues().add(new SubstantiveParametersParameterItem());
    }

    @Action(validate = false)
    public void removeRepeater(String row) {
        int index = Integer.parseInt(row) - 1;
        model.getDto().getValues().remove(index);
    }

    @Action("addTag")
    public void addTag() {
        model.setAddTagClicked(true);
    }

    @Action("columnConditionChanged")
    public void columnConditionChanged()
    {
        model.setAddTagClicked(true);
    }

    @Action("cancelAddTag")
    public void cancelAddTag() {
        model.setAddTagClicked(false);
        // resets old data
        model.setName("");
    }

    @Action(validate = true)
    public void saveTag() {
        if (validate()) {
            SubstantiveParametersTagDto substantiveParametersTagDto = new SubstantiveParametersTagDto();
            substantiveParametersTagDto.setId(UUID.randomUUID().toString());
            substantiveParametersTagDto.setName(model.getName());
            substantiveParametersTagService.persistDto(substantiveParametersTagDto);
            model.setAddTagClicked(false);
            this.model.setSubstantiveParametersTagList(substantiveParametersTagService.listDto(query).stream().map(SubstantiveParametersTagDto::getName).collect(Collectors.toList()));
            this.model.getDto().getTags().add(model.getName());
            // resets old data
            model.setName("");
        }
    }

    private boolean validate() {
        if(model.getSubstantiveParametersTagList().contains(model.getName())) {
            reportValidationError(model,
                "name",
                messageService.getAllBundles().getMessage("parameters.validation.message.tag"),
                PresentationStyleEnum.ERROR);
        }
        return !getUserSession().getValidationResults().hasAtLeastErrors();
    }

//    private boolean isValid() {
//        if (model.getSubstantiveParametersTagList().contains(model.getName())) {
//
//            return false;
//        }
//        return true;
//    }

    /*
    private boolean isValid(String regex, String value) {
        if (regex != null) {
            java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
            java.util.regex.Matcher matcher = pattern.matcher(value);
            return matcher.matches();
        }
        return true;
    }
    */
}
