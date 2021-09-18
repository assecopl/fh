package pl.fhframework.core.rules.builtin;

import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.model.forms.CollectionPageableModel;
import pl.fhframework.model.forms.PageModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Wraps a list with a PageModel.
 */
@BusinessRule(categories = {"collection", "cast"})
public class PageModelWrapList {

    public <T> PageModel<T> pageModelWrapList(Collection<T> list) {
        return new CollectionPageableModel<>(list);
    }
}
