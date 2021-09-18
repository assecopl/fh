package pl.fhframework.core.datasource;

import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.rules.dynamic.model.dataaccess.From;

import java.util.List;
import java.util.Map;

/**
 * Created by pawel.ruta on 2017-05-10.
 */
public interface StoreAccessService {

    <S extends BaseEntity> List<S> storeRead(Class<S> type);

    <S extends BaseEntity> S storeWrite(S baseEntity);

    <S extends BaseEntity> S storeRefresh(S baseEntity);

    <S extends BaseEntity> void storeDelete(S baseEntity);

    <S extends Object> S storeResult(From from);

    <S extends Object> List<S> storeFind(From from);
}
