package pl.fhframework.dp.commons.fh.adm.ui;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.security.AuthorizationManager;
import pl.fhframework.model.forms.Form;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author Tomasz Kozlowski (created on 24.02.2021)
 */
public class FunctionsSelectForm extends Form<FunctionsSelectForm.Model> {

    @Getter
    @Setter
    public static class Model {
        private final FunctionsTreeBuilder functionsTreeBuilder;
        private Set<Node> allSystemFunctions;
        private Node selectedFunctionLeft;
        private Set<AuthorizationManager.Function> addedFunctions = new TreeSet<>();
        private Set<AuthorizationManager.Function> selectedFunctionsRight = new HashSet<>();

        public Model(FunctionsTreeBuilder functionsTreeBuilder) {
            this.functionsTreeBuilder = functionsTreeBuilder;
            this.allSystemFunctions = functionsTreeBuilder.buildSystemFunctionsTree();
        }

        public void addSelectedFunctionsAsAllowed() {
            addSelectedFunctions(false);
        }

        public void addSelectedFunctionsAsDisallowed() {
            addSelectedFunctions(true);
        }

        private void addSelectedFunctions(boolean denial) {
            if (!selectedFunctionLeft.isRoot()) {
                String moduleUUID = selectedFunctionLeft.getModuleUUID();
                String moduleLabel = functionsTreeBuilder.getModuleLabel(moduleUUID);
                AuthorizationManager.Function function =
                        AuthorizationManager.Function.of(selectedFunctionLeft.getFullName(), moduleUUID, moduleLabel, denial);
                addedFunctions.add(function);
            }
        }

        public void removeSelectedFunctions() {
            addedFunctions.removeAll(selectedFunctionsRight);
            selectedFunctionsRight.clear();
        }

    }

}
