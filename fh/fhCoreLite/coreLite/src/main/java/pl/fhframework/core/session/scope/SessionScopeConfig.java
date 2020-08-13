package pl.fhframework.core.session.scope;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration bean of UserSessionScope.
 */
@Configuration
public class SessionScopeConfig {

    /**
     * UserSessionScope registrating bean.
     */
    public static class SessionScopeRegisterer implements BeanFactoryPostProcessor {

        @Override
        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
            beanFactory.registerScope(SessionScope.SESSION_SCOPE, new SessionScope());
        }
    }

    @Bean
    public static BeanFactoryPostProcessor beanFactoryPostProcessor() {
        return new SessionScopeRegisterer();
    }
}
