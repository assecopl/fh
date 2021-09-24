package pl.fhframework.app.preferences;

import org.apache.commons.configuration2.ex.ConfigurationException;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.preferences.UserPreferencesEnum;
import pl.fhframework.core.preferences.UserPreferencesService;
import pl.fhframework.core.uc.IUseCaseNoCallback;
import pl.fhframework.core.uc.IUseCaseNoInput;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.events.BreakLevelEnum;
import pl.fhframework.model.forms.messages.Messages;

import java.util.HashMap;
import java.util.Map;

@UseCase
public class UserPreferencesUC implements IUseCaseNoInput<IUseCaseNoCallback> {
    private UserPreferencesForm.Model model = new UserPreferencesForm.Model();

    private final UserPreferencesService preferencesService;
    private final MessageService messageService;

    public UserPreferencesUC(UserPreferencesService preferencesService, MessageService messageService) {
        this.preferencesService = preferencesService;
        this.messageService = messageService;
    }

    @Override
    public void start() {
        try {
            model.setUseDefaultPrefs(preferencesService.getBooleanPreference(UserPreferencesEnum.USE_DEFAULTS_PREF.getKey(), true));

            model.setDesignerWidth(preferencesService.getIntPreference(UserPreferencesEnum.DESIGNER_WIDTH_PREF.getKey(), 1));
            model.setHideInnerPaddings(preferencesService.getBooleanPreference(UserPreferencesEnum.HIDE_INNER_PADDING_PREF.getKey(), false));
            model.setEditActiveElement(preferencesService.getBooleanPreference(UserPreferencesEnum.EDIT_ACTIVE_ELEMENT_PREF.getKey(), false));
            model.setFilterOutRowFromToolbox(preferencesService.getBooleanPreference(UserPreferencesEnum.FILTER_OUT_ROW_FROM_TOOLBOX_PREF.getKey(), false));
        } catch (ConfigurationException e) {
            Messages.showErrorMessage(getUserSession(), $("fh.preferences.messages.read.error"), e);
        }

        showForm(UserPreferencesForm.class, model);
    }

    @Action
    public void save() {
        try {
            if (model.isUseDefaultPrefs()) {
                preferencesService.clearPreferences();
            } else {
                Map<String, Object> preferences = new HashMap<>();
                preferences.put(UserPreferencesEnum.USE_DEFAULTS_PREF.getKey(), false);
                preferences.put(UserPreferencesEnum.DESIGNER_WIDTH_PREF.getKey(), model.getDesignerWidth());
                preferences.put(UserPreferencesEnum.HIDE_INNER_PADDING_PREF.getKey(), model.isHideInnerPaddings());
                preferences.put(UserPreferencesEnum.EDIT_ACTIVE_ELEMENT_PREF.getKey(), model.isEditActiveElement());
                preferences.put(UserPreferencesEnum.FILTER_OUT_ROW_FROM_TOOLBOX_PREF.getKey(), model.isFilterOutRowFromToolbox());

                preferencesService.setPreferences(preferences);
            }
            Messages.showInfoMessage(getUserSession(), $("fh.preferences.messages.save.success"));
            exit();
        } catch (ConfigurationException e) {
            Messages.showErrorMessage(getUserSession(), $("fh.preferences.messages.save.error"), e);
        }
    }

    @Action(breakOnErrors = BreakLevelEnum.NEVER)
    public void cancel() {
        exit();
    }

    private String $(String key) {
        return messageService.getAllBundles().getMessage(key);
    }
}
