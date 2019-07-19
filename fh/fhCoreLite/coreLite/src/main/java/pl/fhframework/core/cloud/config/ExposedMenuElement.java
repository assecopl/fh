package pl.fhframework.core.cloud.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Menu element exposed to cloud server's clients.
 */
@NoArgsConstructor
@Data
public class ExposedMenuElement {

    public enum ExposedMenuElementType {
        GROUP,
        USE_CASE
    }

    private ExposedMenuElementType type;

    private String label;

    private List<ExposedMenuElement> subelements = new ArrayList<>();

    private String ref;

    private ExposedMenuElement(ExposedMenuElementType type, String label, List<ExposedMenuElement> subelements, String ref) {
        this.type = type;
        this.label = label;
        this.subelements = subelements;
        this.ref = ref;
    }

    public static ExposedMenuElement newUseCaseInfo(String label, String ref, Set<String> allowedSystemFunctions) {
        return new ExposedMenuElement(ExposedMenuElementType.USE_CASE, label, null, ref);
    }

    public static ExposedMenuElement newGroup(String label, List<ExposedMenuElement> subelements) {
        return new ExposedMenuElement(ExposedMenuElementType.GROUP, label, subelements, null);
    }
}
