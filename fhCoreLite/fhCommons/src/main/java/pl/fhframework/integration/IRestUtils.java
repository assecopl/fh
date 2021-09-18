package pl.fhframework.integration;

/**
 * Created by pawel.ruta on 2018-10-11.
 */
public interface IRestUtils {
    RestResource buildRestResource(String endpointName, String endpointUrl, String resourceUri);
}
