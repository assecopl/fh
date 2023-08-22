package pl.fhframework.docs.forms.model.example;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by krzysztof.kobylarek on 2016-11-29.
 */

@AllArgsConstructor
@Setter @Getter
public class Student {
    private Person person;
    private List<Classes> classes;

    public long getId() {
        return hashCode();
    }

    @AllArgsConstructor
    @Setter @Getter
    public static  class Classes {
        private String className;
        private String teacher;
        private List<Grades> grades;

        public long getId() {
            return hashCode();
        }

        @AllArgsConstructor
        @Setter @Getter
        public static class Grades {
            private String grade;
            private String description;

            public long getId() {
                return hashCode();
            }
        }
    }

}
