package pl.fhframework.fhPersistence.maps.features;

import org.hibernate.dialect.function.StandardSQLFunction;

/**
 * Created by pawel.ruta on 2019-02-05.
 */
public class GeoDBDialect extends org.hibernate.spatial.dialect.h2geodb.GeoDBDialect {
    public GeoDBDialect() {
        super();

        registerFunction("transform", new StandardSQLFunction("ST_Transform"));
    }
}
