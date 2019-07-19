package pl.fhframework.aop.services;

/**
 * Created by pawel.ruta on 2018-03-19.
 */
public interface IFhTransactionListener {
    void onStart(Integer propagation, Object owner, Object[] params);

    void onEnd(Integer propagation, Object owner, Object[] outputParams);

    void onError(Integer propagation, Object owner);

    void invalidate();
}
