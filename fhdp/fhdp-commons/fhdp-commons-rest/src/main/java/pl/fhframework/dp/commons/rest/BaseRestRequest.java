package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

//@JsonDeserialize
@Getter @Setter
public class BaseRestRequest<T extends Object> implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;
    private RestObject<T> restObject;



    @JsonIgnore
    public T getRequestData() {
        return this.restObject.getData();
    }

    public void setRequestData(T requestData) {
        this.getRestObject().setData(requestData);
    }

    public BaseRestRequest(String token) {
        this.token = token;
    }

    public BaseRestRequest(String token, T requestData) {
        this.token = token;
        getRestObject().setData(requestData);
    }

    public BaseRestRequest() {
    }

    @JsonDeserialize(using = RestObjectDeserializer.class)
    public RestObject<T> getRestObject() {
        if(restObject == null) {
            restObject = new RestObject<>();
        }
        return restObject;
    }

    public void setRestObject(RestObject<T> restObject) {
        if(this.restObject == null && restObject != null) {
            this.restObject = restObject;
        }
    }
}
