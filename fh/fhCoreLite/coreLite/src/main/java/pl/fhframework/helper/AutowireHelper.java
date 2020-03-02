package pl.fhframework.helper;

import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * Helper class which is able to autowire a specified class. It holds a static reference to the {@link org
 * .springframework.context.ApplicationContext}.
 */
@Component
public class AutowireHelper implements ApplicationContextAware {
 
    private static final AutowireHelper INSTANCE = new AutowireHelper();
    private static ApplicationContext applicationContext;
    private static AutowireCapableBeanFactory autowireFactory;
    private static boolean enabled = true;
 
    private AutowireHelper() {
    }

    /**
     * Gets application property from Spring context
     * @param key property key
     * @return property value
     */
    public static String getApplicationProperty(String key) {
        return AutowireHelper.applicationContext.getEnvironment().getProperty(key);
    }

    /**
     * Looks up and returns a bean of given type from context.
     * @param beanClass bean class
     * @param <T> bean type
     * @return a bean
     */
    public static <T> T getBean(Class<T> beanClass) {
        return getAutowireFactory().getBean(beanClass);
    }
 
    /**
     * Tries to autowire the specified instance of the class if one of the specified beans which need to be autowired
     * are null.
     *
     * @param classToAutowire the instance of the class which holds @Autowire annotations
     * @param beansToAutowireInClass the beans which have the @Autowire annotation in the specified {#classToAutowire}
     */
    public static void autowire(Object classToAutowire, Object... beansToAutowireInClass) {
        if (!enabled) {
            return;
        }
        for (Object bean : beansToAutowireInClass) {
            if (bean == null) {
                forceAutowire(classToAutowire);
                return;
            }
        }
    }

    /**
     * Tries to autowire the specified instance of the class.
     *
     * @param classToAutowire the instance of the class which holds @Autowire annotations
     */
    public static void forceAutowire(Object classToAutowire) {
        getAutowireFactory().autowireBean(classToAutowire);
    }

    /**
     * Creates a new instance and autowires dependencies of a given class.
     * @param beanClass class
     * @param <T> type
     * @return a new autowired instance
     */
    public static <T> T createAndAutoWire(Class<T> beanClass) {
        return getAutowireFactory().createBean(beanClass);
    }
 
    @Override
    public void setApplicationContext(final ApplicationContext applicationContext) {
        AutowireHelper.applicationContext = applicationContext;
    }

    public static void setAutowireFactory(AutowireCapableBeanFactory autowireFactory) {
        AutowireHelper.autowireFactory = autowireFactory;
    }

    private static AutowireCapableBeanFactory getAutowireFactory() {
        return AutowireHelper.applicationContext != null ? applicationContext.getAutowireCapableBeanFactory() : autowireFactory;
    }

    /**
     * @return the singleton instance.
     */
    public static AutowireHelper getInstance() {
        return INSTANCE;
    }

    public static void disable() {
        enabled = false;
    }
}