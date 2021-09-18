package pl.fhframework.integration;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-04-12.
 */
public interface IEndpointAccess {
    List<String> findAllNames();

    String findOneUrlByName(String name);
}
