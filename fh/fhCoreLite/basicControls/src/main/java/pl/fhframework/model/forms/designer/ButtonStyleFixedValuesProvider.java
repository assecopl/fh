package pl.fhframework.model.forms.designer;

/**
 * Button styled values provider
 */
public class ButtonStyleFixedValuesProvider extends StaticDesignerFixedValuesProvider {

    private static final String[] STYLES = {"default", "primary", "success", "info", "warning", "danger", "link", "dark", DefaultDesignerFixedValuesProvider.EMPTY_BINDING_OPTION};

    public ButtonStyleFixedValuesProvider() {
        super(true, true, true, STYLES);
    }
}
