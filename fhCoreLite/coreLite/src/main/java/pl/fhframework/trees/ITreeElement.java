package pl.fhframework.trees;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public interface ITreeElement {
    //settery dodane do interfejsu ze wzgledu na brak obslugi bindingu gdy element modelu fromatki jest interfejsem

    String getRef();
    void setRef(String ref);

    String getIcon();
    void setIcon(String icon);

    int getPosition();
    void setPosition(int position);

    String getLabel();
    void setLabel(String label);

    String getDescription();
    void setDescription(String description);

    boolean isDynamic();

    boolean isLeaf();

    List<String> getModes();
    void setModes(List<String> modes);

    String getCloudServerName();

    AtomicBoolean getActivityToken();

    boolean isGrouping();

    List<ITreeElement> getSubelements();

    default boolean isAvailableForMode(List<String> activeModes) {
        if (!activeModes.isEmpty()) {
            if (this.getModes() == null || this.getModes().isEmpty() || !Collections.disjoint(activeModes, this.getModes())) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }
}
