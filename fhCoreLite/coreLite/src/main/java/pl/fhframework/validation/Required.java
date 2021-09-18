package pl.fhframework.validation;

public @interface Required {
    boolean value() default true;
}
