package pl.fhframework.fhPersistence.conversation;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import pl.fhframework.aop.services.FhAspectsOrder;
import pl.fhframework.fhPersistence.core.EntityManagerRepository;

/**
 * @author Paweł Ruta
 */
@Aspect
@Component
@Order(FhAspectsOrder.SESSION_CONVERSATION)
public class SessionConversationManagerAspect {
    @Autowired
    EntityManagerRepository entityManagerRepository;

    @Around("@annotation(pl.fhframework.fhPersistence.anotation.WithoutConversation)")
    public Object withoutSessionConversationCall(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            entityManagerRepository.turnOffSessionConvesation(joinPoint.getTarget());
            return joinPoint.proceed();
        }
        finally {
            entityManagerRepository.turnOnSessionConvesation(joinPoint.getTarget());
        }
    }
}

