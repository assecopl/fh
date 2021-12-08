package pl.fhframework.dp.commons.fh.wrapper;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class NestedWrapper<T, N> extends Wrapper<T> {

    private List<Wrapper<N>> nested;

    // fixes luna getter problem
    public List<Wrapper> getNested() {
        return new ArrayList<>(nested);
    }

    // fixes luna setter problem
    public void setNested(List<Wrapper> nested) {
        ArrayList<Wrapper<N>> wrappers = new ArrayList<>();
        for (Wrapper wrapper : nested) {
            wrappers.add(wrapper);
        }
        this.nested = wrappers;
    }

    public NestedWrapper() {
    }

    public NestedWrapper(T element, T oldElement, List<Wrapper<N>> nested) {
        super(element, oldElement, false, false);
        this.nested = nested;
    }

    public NestedWrapper(T element, T oldElement, List<Wrapper<N>> nested, Boolean disabled, Boolean isLastValueEnabled) {
        super(element, oldElement, disabled, isLastValueEnabled);
        this.nested = nested;
    }
}
