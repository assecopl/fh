package pl.fhframework.core.uc;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.stereotype.Component;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.core.FhUseCaseException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.StringUtils;

import java.lang.reflect.Modifier;
import java.util.Objects;

@Component
public class UseCaseBeanFactoryPostProcessor implements BeanFactoryPostProcessor {
    private ConfigurableListableBeanFactory beanFactory;

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory configurableListableBeanFactory) throws BeansException {
        this.beanFactory = configurableListableBeanFactory;

        for (String beanName : configurableListableBeanFactory.getBeanDefinitionNames()) {
            Class<?> beanClazz = configurableListableBeanFactory.getType(beanName);
            if (!Modifier.isAbstract(beanClazz.getModifiers()) && IUseCase.class.isAssignableFrom(beanClazz)) {
                UseCase useCaseAnnotation = beanClazz.getAnnotation(UseCase.class);
                if (useCaseAnnotation == null) {
                    throw new FhUseCaseException("Use case class [" + beanClazz.getName() + "] must be annotated with @UseCase.");
                }
                BeanDefinition beanDefinition = configurableListableBeanFactory.getBeanDefinition(beanName);
                if (!beanDefinition.isPrototype()) {
                    throw new FhUseCaseException("Use case class annotated with @UseCase [" + beanClazz.getName() + "] must be of scope prototype.");
                }
            }
        }
    }

    public void registerBean(Class<?> clazz) {
        registerBean(clazz, false);
    }

    public void registerBean(Class<?> clazz, boolean lazyInit) {
        registerBean(clazz, StringUtils.decapitalize(clazz.getName()), lazyInit, false);
    }

    public void registerBean(Class<?> clazz, String beanName, boolean lazyInit, boolean registerOld) {
        BeanDefinitionRegistry registry = ((BeanDefinitionRegistry ) beanFactory);

        // @FeignClient is causing race condition on beans name, leave old bean name
        /*String oldBeanName = StringUtils.decapitalize(clazz.getName());
        if (!oldBeanName.equals(beanName) && registry.isBeanNameInUse(oldBeanName)) {
            registry.removeBeanDefinition(oldBeanName);
        }*/
        boolean redefinition = registry.isBeanNameInUse(beanName);
        if (redefinition) {
            if (registerOld) {
                Class<?> oldBean = ReflectionUtils.getRealClass(beanFactory.getBean(beanName));
                if (!Objects.equals(oldBean, clazz)) {
                    registerBean(oldBean, lazyInit);
                }
            }
            registry.removeBeanDefinition(beanName);
            FhLogger.warn("Redefining bean '{}'", beanName);
        }

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(clazz);
        beanDefinition.setLazyInit(lazyInit);
        beanDefinition.setAbstract(false);
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setScope("prototype");

        registry.registerBeanDefinition(beanName, beanDefinition);
        if (redefinition) {
            // because of cache in SmartInstantiationAwareBeanPostProcessor -> AbstractAutoProxyCreator.proxyTypes,
            // first getBean by name, this will change cache in AbstractAutoProxyCreator and allow use of getBean by class
            beanFactory.getBean(beanName); // populate cache with new type
        }
    }

    public <T> T registerBean(T object) {
        beanFactory.autowireBean(object);
        return (T) beanFactory.initializeBean(object, String.format("%s-%s", object.getClass().getSimpleName(), System.identityHashCode(object)));
    }
}
