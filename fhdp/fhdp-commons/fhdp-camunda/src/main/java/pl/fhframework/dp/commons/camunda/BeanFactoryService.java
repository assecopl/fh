package pl.fhframework.dp.commons.camunda;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

/**
 * Klasa dostępu do kontekstu springa
 */
@Service
public class BeanFactoryService implements BeanFactoryAware {

    private static BeanFactory beanFactory;

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        BeanFactoryService.beanFactory = beanFactory;
    }

    public static BeanFactory getBeanFactory() {
        return BeanFactoryService.beanFactory;
    }

    public static <T> T getBean(Class<T> type) {
        return beanFactory != null ? (T) beanFactory.getBean(type) : null;
    }

    public static <T> T getBean(String name, Class<T> type) {
        return beanFactory != null ? (T) beanFactory.getBean(name, type) : null;
    }

    public static Object getBean(String name, Object... par) {
        return beanFactory != null ? beanFactory.getBean(name, par) : null;
    }

    public static boolean containsBean(String name) {
        return beanFactory != null ? beanFactory.containsBean(name): false;
    }
}