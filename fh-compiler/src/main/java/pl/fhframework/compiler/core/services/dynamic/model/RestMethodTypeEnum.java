package pl.fhframework.compiler.core.services.dynamic.model;

import org.springframework.http.HttpMethod;

public enum RestMethodTypeEnum {
    GET("GET", HttpMethod.GET),
    POST("POST", HttpMethod.POST),
    POST_APP_FROM("POST APP FORM", HttpMethod.POST),
    PUT("PUT", HttpMethod.PUT),
    DELETE("DELETE", HttpMethod.DELETE),
    ;

    private String opis;

    private HttpMethod httpMethod;

    RestMethodTypeEnum(String opis, HttpMethod httpMethod) {
        this.opis = opis;
        this.httpMethod = httpMethod;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    @Override
    public String toString() {
        return opis;
    }
}
