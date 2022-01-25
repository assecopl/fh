package pl.fhframework.dp.commons.rest;

public class RestObject<T extends Object> {

    private T data;

    private Class className;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Class getClassName() {
        return data != null ? data.getClass() : null;
    }

    public void setClassName(Class className) {
    }
}
