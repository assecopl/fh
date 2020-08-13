package pl.fhframework.core.logging.processor;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.stereotype.Service;

import pl.fhframework.core.logging.ErrorInformation;

/**
 * Created by Adam Zareba on 30.01.2017.
 */
@Service
public class ErrorInformationProcessorImpl implements IErrorInformationProcessor {

    @Override
    public boolean process(ErrorInformation errorInformation) {
        if (isTypeOf(errorInformation.getException(), OptimisticLockingFailureException.class)) {
            errorInformation.setErrorType(ErrorInformation.ErrorType.EXTERNAL_DATA_CHANGE);
        }

        return true;
    }
}
