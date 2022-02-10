/*
 * MessageType.java
 *
 * Created on 3 styczeń 2007, 14:13
 *
 */

package pl.fhframework.dp.commons.rest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author <a href="mailto:dariusz_skrudlik@skg.pl">Dariusz Skrudlik</a>
 * @version $Revision: 1552 $, $Date: 2009-06-25 13:39:06 +0200 (Cz, 25 cze 2009) $
 */
public class BaseRestResponse<E,F> implements Serializable {
    private static final long serialVersionUID = 1L;
    
    protected BaseRestResponse() {
    }

    private RestObject<E> restObject;
    private RestListOfObjects<F> restList;
    private Long count;


    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    /**
     * Obiekt typu E zwrócony w wyniku obsługi.
     */
    private E object = null;
    
    @JsonIgnore
    public E getObject() {
        return getRestObject().getData();
    }
    
    public void setObject(E object) {
        getRestObject().setData(object);
    }
    
    /**
     * Lista obiektów zwrócona w wyniku obsługi.
     */
    private List<F> list = null;

    @JsonIgnore
    public List<F> getList() {
        return getRestList().getList();
    }
    
    public void setList(List<F> list) {getRestList().setList(list);
    }

    /**
     * Komunikat o błędzie.
     */
    private String message = null;
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }

    private String messageKey = null;

    //Informacja która powinna zostać przetłumaczona na podstawie resorców.
    public String getMessageKey() {
        return messageKey;
    }

    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }

    /**
     * Informuje czy podczas obsługi nie wystąpił błąd!
     * @return - true, błąd - komunikat w message
     */
    private boolean valid = true;
    
    public boolean isValid() {
        return valid;
    }
    
    public void setValid(boolean valid) {
        this.valid = valid;
    }

    /**
     * stack trace when exception occurs
     */
    private String stackTrace;

    /**
     * Get stack trace
     * @return the stackTrace
     */
    public String getStackTrace() {
        return stackTrace;
    }

    /**
     * Set stack trace
     * @param stackTrace - the stackTrace to set
     */
    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    @JsonDeserialize(using = RestObjectDeserializer.class)
    public RestObject<E> getRestObject() {
        if(restObject == null) {
            restObject = new RestObject<E>();
        }
        return restObject;
    }

    public void setRestObject(RestObject<E> restObject) {
        if(this.restObject == null && restObject !=null) {
            this.restObject = restObject;
        }
    }

    @JsonDeserialize(using = RestListOfObjectDeserializer.class)
    public RestListOfObjects<F> getRestList() {
        if(restList == null) {
            restList = new RestListOfObjects<F>();
        }
        return restList;
    }

    public void setRestList(RestListOfObjects<F> restList) {
        if(this.restList == null && restList !=null) {
            this.restList = restList;
        }
    }

//    @Override
//    public String toString() {
//        return ToStringBuilder.reflectionToString(this);
//    }
}
