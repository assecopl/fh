package pl.fhframework.dp.commons.fh.model;

import pl.fhframework.model.forms.PageModel;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-08-02
 */
public abstract class GenericListPagedModel<T, Q> {
    PageModel<T> list;
    T selectedElement;
    Q query;

    public PageModel<T> getList() {
        return list;
    }

    public void setList(PageModel<T> list) {
        this.list = list;
    }

    public T getSelectedElement() {
        return selectedElement;
    }

    public void setSelectedElement(T selectedElement) {
        this.selectedElement = selectedElement;
    }

    public Q getQuery() {
        return query;
    }

    public void setQuery(Q query) {
        this.query = query;
    }
}
