package pl.fhframework.modules.services;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
public class ServiceLocator {
    protected static IServiceLocator INSTANCE;

    public static IServiceLocator getInstance() {
        return INSTANCE;
    }
}
