package pl.fhframework.core.rules.builtin;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.CoreSystemFunction;
import pl.fhframework.core.datasource.StoreAccessService;
import pl.fhframework.core.model.BaseEntity;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.security.annotations.SystemFunction;

/**
 * Created by pawel.ruta on 2017-10-23.
 */
@BusinessRule(categories = {"data", "database"})
public class DataAccessUtils {
    @Autowired(required = false)
    StoreAccessService storeAccessService;

    @SystemFunction(CoreSystemFunction.CORE_RULES_PERSISTENCE_WRITE)
    public void storeWrite(BaseEntity baseEntity) {
        storeAccessService.storeWrite(baseEntity);
    }

    @SystemFunction(CoreSystemFunction.CORE_RULES_PERSISTENCE_READ)
    public void storeRefresh(BaseEntity baseEntity) {
        storeAccessService.storeRefresh(baseEntity);
    }

    @SystemFunction(CoreSystemFunction.CORE_RULES_PERSISTENCE_DELETE)
    public void storeDelete(BaseEntity baseEntity) {
        storeAccessService.storeDelete(baseEntity);
    }
}

