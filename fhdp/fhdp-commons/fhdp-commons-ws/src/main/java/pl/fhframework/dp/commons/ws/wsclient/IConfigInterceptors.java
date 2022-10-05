package pl.fhframework.dp.commons.ws.wsclient;

import org.apache.cxf.endpoint.Client;

public interface IConfigInterceptors {

    void addInterceptor(Client client);

}
