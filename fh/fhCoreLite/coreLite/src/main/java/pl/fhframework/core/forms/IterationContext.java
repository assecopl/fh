package pl.fhframework.core.forms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.model.forms.Component;

/**
 * Created by krzysztof.kobylarek on 2016-12-01.
 */
@AllArgsConstructor
@Getter @Setter
public class IterationContext {
    private Integer index;
    private Component component;

    @Override
    public boolean equals(Object obj) {
        if (index!=null && component!=null && obj instanceof  IterationContext){
            IterationContext ctx = IterationContext.class.cast(obj);
            return ctx.getIndex()==index && ctx.getComponent().equals(component);
        }
        return false;
    }
}
