package pl.fhframework.compiler.core.dynamic;

import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.ResolvableType;
import pl.fhframework.ReflectionUtils;

import java.util.*;

/**
 * Simple, partially implemented autowire factory for predefined beans. Used in maven environment.
 */
public class MavenAutowireFactory implements AutowireCapableBeanFactory {

    private Map<Class<?>, Object> beansMap = new LinkedHashMap<>();
    private List<Object> beans = new ArrayList<>();

    /**
     * Puts bean implementation into the factory
     * @param instance instance
     * @param classOrInterface type to be injected as
     * @param otherClassesOrInterfaces other types to be injected as
     */
    public void putBean(Object instance, Class<?> classOrInterface, Class<?>... otherClassesOrInterfaces) {
        beansMap.put(classOrInterface, instance);
        for (Class<?> otherClassOrInterface : otherClassesOrInterfaces) {
            beansMap.put(otherClassOrInterface, instance);
        }
        beans.add(instance);
    }

    /**
     * Autowires all factory beans.
     */
    public void crossWireBeans() {
        beans.forEach(this::autowireBean);
    }

    // ------------------ IMPLEMENTED METHODS --------------------

    @Override
    public <T> T createBean(Class<T> beanClass) throws BeansException {
        T bean = ReflectionUtils.newInstance(beanClass);
        autowireBean(bean);
        return bean;
    }

    @Override
    public void autowireBean(Object existingBean) throws BeansException {
        org.springframework.util.ReflectionUtils.doWithFields(existingBean.getClass(), field -> {
            Object dependencyInstance = beansMap.get(field.getType());
            field.setAccessible(true);
            if (dependencyInstance != null && field.get(existingBean) == null) {
                field.set(existingBean, dependencyInstance);
            }
        }, field -> field.isAnnotationPresent(Autowired.class));
    }

    @Override
    public Object configureBean(Object existingBean, String beanName) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return createBean(beanClass);
    }

    @Override
    public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
        return createBean(beanClass);
    }

    @Override
    public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
        autowireBean(existingBean);
    }

    @Override
    public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object initializeBean(Object existingBean, String beanName) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void destroyBean(Object existingBean) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object resolveBeanByName(String s, DependencyDescriptor dependencyDescriptor) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor, String requestingBeanName, Set<String> autowiredBeanNames, TypeConverter typeConverter) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getBean(String name) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getBean(Class<T> requiredType) throws BeansException {
        return (T) beansMap.get(requiredType);
    }

    @Override
    public Object getBean(String name, Object... args) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(Class<T> aClass) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> ObjectProvider<T> getBeanProvider(ResolvableType resolvableType) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsBean(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Class<?> getType(String s, boolean b) throws NoSuchBeanDefinitionException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getAliases(String name) {
        throw new UnsupportedOperationException();
    }
}
