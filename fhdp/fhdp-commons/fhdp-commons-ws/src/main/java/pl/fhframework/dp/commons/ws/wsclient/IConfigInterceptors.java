package pl.fhframework.dp.commons.ws.wsclient;

import org.apache.cxf.endpoint.Client;

/**
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version :  $, :  $
 * @created 08/01/2020
 */
public interface IConfigInterceptors {

    void addInterceptor(Client client);

}
