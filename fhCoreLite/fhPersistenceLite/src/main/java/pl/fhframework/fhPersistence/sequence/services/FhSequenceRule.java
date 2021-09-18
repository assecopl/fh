package pl.fhframework.fhPersistence.sequence.services;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.FhException;
import pl.fhframework.core.rules.BusinessRule;
import pl.fhframework.core.uc.Parameter;

/**
 * Created by pawel.ruta on 2018-02-07.
 */
@BusinessRule(categories = "database")
public class FhSequenceRule {
    @Autowired
    private FhSequenceHelper helper;

    public long sequenceNextValue(@Parameter(name = "sequenceName") String sequenceName) {
        Long value = helper.sequenceNextValue(sequenceName);

        if (value == null) {
            Exception createException = null;
            try {
                helper.sequenceCreate(sequenceName);
            } catch (Exception uc) {
                // can be unique constraint exception
                createException = uc;
            }

            value = helper.sequenceNextValue(sequenceName);

            if (value == null){
              if (createException != null) {
                  throw new FhException(String.format("Error retrieving sequence '%s'", sequenceName), createException);
              }
              else {
                  throw new FhException(String.format("Unknown error retrieving sequence '%s'", sequenceName));
              }
            }
        }

        return value;
    }
}
