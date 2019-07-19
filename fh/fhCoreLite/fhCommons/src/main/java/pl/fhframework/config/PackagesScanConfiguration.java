package pl.fhframework.config;

/**
 * Created by pawel.ruta on 2018-05-07.
 */
public interface PackagesScanConfiguration extends IFhConfiguration {
    default String[] additionalComponentPackages() {
        return new String[]{};
    }

    default String[] additionalRepositoryPackages() {
        return new String[]{};
    }

    default String[] additionalEntityPackages() {
        return new String[]{};
    }
}
