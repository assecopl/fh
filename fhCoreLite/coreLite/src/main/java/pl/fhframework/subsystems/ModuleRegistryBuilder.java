package pl.fhframework.subsystems;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ModuleRegistryBuilder {

    private List<String> names = new ArrayList<>();
    private List<String> namesExcluded = new ArrayList<>();

    public ModuleRegistryBuilder autodetect() {
        Set<String> fhModulesNameOnClasspath = ModuleRegistry.getFhModulesNameOnClasspath();
        names.addAll(fhModulesNameOnClasspath);

        return this;
    }

    public ModuleRegistryBuilder add(String moduleName) {
        names.add(moduleName);
        return this;
    }

    public ModuleRegistryBuilder exclude(String moduleName) {
        namesExcluded.add(moduleName);
        return this;
    }

    @SuppressWarnings("deprecation")
    public void load() {
        // remove duplicate
        LinkedHashSet<String> forLoad = new LinkedHashSet<>(names);

        // remove excluded
        forLoad.removeAll(namesExcluded);

        for (String moduleName : forLoad) {
            ModuleRegistry.loadModule(moduleName);
        }
    }

}
