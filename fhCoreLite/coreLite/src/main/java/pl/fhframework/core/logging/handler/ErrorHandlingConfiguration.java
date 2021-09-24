package pl.fhframework.core.logging.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.fhframework.core.logging.handler.IErrorInformationHandler;
import pl.fhframework.ReflectionUtils;

/**
 * Created by Adam Zareba on 31.01.2017.
 */
@Configuration
public class ErrorHandlingConfiguration {
    @Value("${system.error.handler:pl.fhframework.core.logging.handler.NotificationErrorInformationHandler}")
    private String errorHandlerClass;

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public IErrorInformationHandler errorInformationHandler() {
        return (IErrorInformationHandler) applicationContext.getBean(ReflectionUtils.getClassForName(errorHandlerClass));
    }
}
