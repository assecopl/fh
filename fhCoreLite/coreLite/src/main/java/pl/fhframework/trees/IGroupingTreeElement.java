package pl.fhframework.trees;

import java.util.Comparator;

public interface IGroupingTreeElement extends ITreeElement {

    String getContainerId();

    //Class<? extends IGroupingTreeElement> getContainer();

    void addSubelement(ITreeElement newElement);

    void sortSubelements(Comparator<ITreeElement> comparer);

    default boolean isGrouping() {
        return true;
    }
}
