package pl.fhframework.dp.commons.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class RestListOfObjects<F extends Object> {

    private F data;

    private Class className;

    private List<F> list = null;

    public List<F> getList() {
        return this.list;
    }

    public void setList(List<F> list) {
        this.list = list;
    }


    public List<Class> getClassName() {
        Set set = list != null && !list.isEmpty() ? list.stream().map(x -> x.getClass()).collect(Collectors.toSet()) : null;

        if (set != null) {
            if (set.size() == 1) {
                return new ArrayList<>(set);
            } else {
                return list.stream().map(x -> x.getClass()).collect(Collectors.toList());
            }
        }
        return null;
    }

    public void setClassName(Class className) {
    }
}
