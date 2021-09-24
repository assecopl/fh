package pl.fhframework.modules.services;

/**
 * Created by pawel.ruta on 2018-10-19.
 */
public interface IDescribableService<T> {
    default T getDescriptor() {
        return ServiceLocator.getInstance().getDescriptor((Class<? extends IDescribableService<T>>) this.getClass());
    }
}
