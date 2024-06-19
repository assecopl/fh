package pl.fhframework.docs.exception.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Created by k.czajkowski on 01.03.2017.
 */
@Getter
@AllArgsConstructor
public class DescribedClass {

    private String name;
    private String packageName;
    private String description;

    @Setter
    private List<DescribedConstructor> constructors;

    public DescribedClass(String name, String packageName, String description) {
        this.name = name;
        this.packageName = packageName;
        this.description = description;
    }

    @Getter
    @AllArgsConstructor
    public static class DescribedConstructor {

        private String declaration;
        private String description;
        @Setter
        private List<DescribedParameter> parameters;

        public DescribedConstructor(String declaration, String description) {
            this.declaration = declaration;
            this.description = description;
        }
    }

    @Getter
    @AllArgsConstructor
    public static class DescribedParameter {
        String className;
        String parameterName;
        String description;
    }
}
