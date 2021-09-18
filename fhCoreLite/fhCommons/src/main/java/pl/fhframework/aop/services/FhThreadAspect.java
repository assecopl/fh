package pl.fhframework.aop.services;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhExternalServiceException;
import pl.fhframework.core.FhServiceConnectionException;

import java.net.ConnectException;
import java.util.List;

/**
 * @author Paweł Ruta
 */
@Aspect
@Component
@Order(FhAspectsOrder.SESSION)
public class FhThreadAspect {
    @Autowired(required = false)
    private List<IFhThreadService> fhService;

    @Autowired(required = false)
    private IFhSessionService sessionService;


    @Around("within(@pl.fhframework.core.services.FhService *) && execution(public * *(..))")
    public Object serviceOperationPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        boolean newSession = false;

        Object retVal;

        try {
            Object[] inputArgs = joinPoint.getArgs();
            if (sessionService.onServiceStart(((MethodSignature)joinPoint.getSignature()).getMethod(), joinPoint.getTarget(), inputArgs)) {
                newSession = true;
                threadServiceStart();
            }

            retVal = joinPoint.proceed(inputArgs);

        }
        catch (Exception e) {
            sessionService.onError(((MethodSignature)joinPoint.getSignature()).getMethod(), joinPoint.getTarget(), e, newSession);

            Throwable rootCause = FhException.getRootCause(e);
            if (rootCause instanceof ConnectException) {
                throw new FhServiceConnectionException(rootCause);
            }
            else if (rootCause instanceof RestClientException) {
                throw new FhExternalServiceException(rootCause);
            }
            throw e;
        }
        finally {
            if (newSession) {
                threadServiceStop();
            }
        }

        Object[] retValHolder = new Object[] {retVal};
        sessionService.onServiceEnd(((MethodSignature)joinPoint.getSignature()).getMethod(), joinPoint.getTarget(), retValHolder, newSession);

        return retValHolder[0];
    }

    @Around("@annotation(pl.fhframework.aop.services.FhThreadHandler)")
    public Object fhThreadService(ProceedingJoinPoint joinPoint) throws Throwable {

        try {
            threadServiceStart();

            return joinPoint.proceed();
        } finally {
            threadServiceStop();
        }
    }

    private void threadServiceStop() {
        if (fhService != null) {
            for (int i = fhService.size() - 1; i >= 0; i--) {
                fhService.get(i).onThreadEnd();
            }
        }
    }

    private void threadServiceStart() {
        if (fhService != null) {
            for (int i = 0; i < fhService.size(); i++) {
                fhService.get(i).onThreadStart();
            }
        }
    }
}
