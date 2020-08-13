package pl.fhframework.app;

import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.filter.AssignableTypeFilter;
import pl.fhframework.ReflectionUtils;
import pl.fhframework.app.config.FhApplication;
import pl.fhframework.config.PackagesScanConfiguration;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhFrameworkException;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.JsonUtil;
import pl.fhframework.subsystems.ModuleRegistry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static pl.fhframework.ReflectionUtils.createClassObject;

@FhApplication
public class DefaultApplication extends SpringBootServletInitializer {
    private static final String SPRING_SCAN_CONFIGURATION_FILE_PATH = "packagesScanConfiguration.json";

    public static void main(String... args) {
        fhApplicationInit();
        SpringApplication.run(DefaultApplication.class, args);
    }

    /** Method is used when application is deployed on an external application server, e.g. WildFly */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        fhApplicationInit();
        return super.configure(builder);
    }

    public static void fhApplicationInit() {
        FhCL.init(Thread.currentThread().getContextClassLoader());
        registerModules();
        preConfigureSpring();
    }

    protected static void registerModules() {
        ModuleRegistry.registry()
                .autodetect()
                .load();
    }

    protected static void preConfigureSpring() {
        List<String> basePackages = ModuleRegistry.getLoadedModules().stream().map(subsystem -> subsystem.getBasePackage()).collect(Collectors.toList());
        String property = basePackages.stream().collect(Collectors.joining(","));

        String componentsProperty = property;
        String repositoriesProperty = property;
        String entitiesProperty = property;

        ClassPathScanningCandidateComponentProvider classPathScanner = new ClassPathScanningCandidateComponentProvider(false);
        classPathScanner.addIncludeFilter(new AssignableTypeFilter(PackagesScanConfiguration.class));
        for (String basePackage : basePackages) {
            List<Class<? extends PackagesScanConfiguration>> packagesConfigClasses = ReflectionUtils.giveClassesTypeList(basePackage, PackagesScanConfiguration.class);

            for (Class<? extends PackagesScanConfiguration> clazz : packagesConfigClasses) {
                PackagesScanConfiguration scanConfiguration = createClassObject(clazz);
                componentsProperty = joinPackages(componentsProperty, joinPackages(scanConfiguration.additionalComponentPackages()));
                repositoriesProperty = joinPackages(repositoriesProperty, joinPackages(scanConfiguration.additionalRepositoryPackages()));
                entitiesProperty = joinPackages(entitiesProperty, joinPackages(scanConfiguration.additionalEntityPackages()));
            }
        }

        PackagesScan additionalPackagesScan = readPackagesScan();
        componentsProperty = joinPackages(componentsProperty, joinPackages(additionalPackagesScan.getComponentPackages().toArray(new String[]{})));
        repositoriesProperty = joinPackages(repositoriesProperty, joinPackages(additionalPackagesScan.getRepositoryPackages().toArray(new String[]{})));
        entitiesProperty = joinPackages(entitiesProperty, joinPackages(additionalPackagesScan.getEntityPackages().toArray(new String[]{})));

        System.setProperty("fh.component.scan", joinPackages(componentsProperty, System.getProperty("fh.component.scan")));
        System.setProperty("fh.repository.scan", joinPackages(repositoriesProperty, System.getProperty("fh.repository.scan")));
        System.setProperty("fh.entity.scan", joinPackages(entitiesProperty, System.getProperty("fh.entity.scan")));
    }

    private static String joinPackages(String... packages) {
        return Arrays.stream(packages).filter(Objects::nonNull).collect(Collectors.joining(","));
    }

    @Data
    protected static class PackagesScan {
        List<String> componentPackages = new ArrayList<>();

        List<String> repositoryPackages = new ArrayList<>();

        List<String> entityPackages = new ArrayList<>();
    }

    protected static PackagesScan readPackagesScan() {
        PackagesScan additionalPackagesScan = new PackagesScan();

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Resource> resources = new ArrayList<>();
        try {
            resources.addAll(Arrays.asList(resolver.getResources(PathMatchingResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + SPRING_SCAN_CONFIGURATION_FILE_PATH)));

            for (Resource moduleResource : resources) {
                PackagesScan packagesScan = JsonUtil.readValue(new String(FhResource.get(moduleResource).getContent()), PackagesScan.class);
                additionalPackagesScan.getComponentPackages().addAll(packagesScan.getComponentPackages());
                additionalPackagesScan.getRepositoryPackages().addAll(packagesScan.getRepositoryPackages());
                additionalPackagesScan.getEntityPackages().addAll(packagesScan.getEntityPackages());
            }
        } catch (IOException exception) {
            FhLogger.error("Cannot load Spring scan configuration", exception);
            throw new FhFrameworkException(exception);
        }

        return additionalPackagesScan;
    }
}
