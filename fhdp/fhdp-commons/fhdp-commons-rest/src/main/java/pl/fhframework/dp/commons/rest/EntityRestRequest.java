package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public class EntityRestRequest<ENTITY extends Object, QUERY extends Object, KEY extends Object> extends  BaseRestRequest<ENTITY> {

    public EntityRestRequest() {
    }


    private OperationTypeEnum operationType;
    private RestObject<QUERY> restQueryObject;
    private RestObject<KEY> restEntityKeyObject;
    private String dtoName;

   @JsonIgnore
    public QUERY getQuery() {
        return getRestQueryObject().getData();
    }

    public void setQuery(QUERY query) {
        getRestQueryObject().setData(query);
    }

    @JsonIgnore
    public KEY getEntityKey() {
       return getRestEntityKeyObject().getData();
    }

    public void setEntityKey(KEY entityKey) {
        getRestEntityKeyObject().setData(entityKey);
    }

    public OperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    @JsonDeserialize(using = RestObjectDeserializer.class)
    public RestObject<QUERY> getRestQueryObject() {
       if(restQueryObject == null) {
           restQueryObject = new RestObject<QUERY>();
       }
       return restQueryObject;
    }

    public void setRestQueryObject(RestObject<QUERY> restQueryObject) {
       if(this.restQueryObject == null && restQueryObject != null) {
           this.restQueryObject = restQueryObject;
       }
    }

    @JsonDeserialize(using = RestObjectDeserializer.class)
    public RestObject<KEY> getRestEntityKeyObject() {
        if(restEntityKeyObject == null) {
            restEntityKeyObject = new RestObject<KEY>();
        }
        return restEntityKeyObject;
    }

    public void setRestEntityKeyObject(RestObject<KEY> restEntityKeyObject) {
        if(this.restEntityKeyObject == null && restEntityKeyObject != null) {
            this.restEntityKeyObject = restEntityKeyObject;
        }
    }

    public String getDtoName() {
        return dtoName;
    }

    public void setDtoName(String dtoName) {
        this.dtoName = dtoName;
    }

    public void setDtoClass(Class dtoClass) {
        if(dtoClass != null) {
            dtoName = dtoClass.getSimpleName();
        }
    }

}
