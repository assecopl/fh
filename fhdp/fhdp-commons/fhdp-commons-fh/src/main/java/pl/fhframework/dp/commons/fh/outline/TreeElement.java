package pl.fhframework.dp.commons.fh.outline;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class TreeElement<T> {
    private String label;
    private String icon;
    private T obj;

    private List<TreeElement<T>> children = new ArrayList<>();

    public TreeElement(String label, String icon, T obj) {
        this.label = label;
        this.obj = obj;
    }

    public void addChild(TreeElement<T> element) {
        children.add(element);
    }

}
