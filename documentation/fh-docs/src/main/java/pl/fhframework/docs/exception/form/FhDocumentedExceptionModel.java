package pl.fhframework.docs.exception.form;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.core.FhException;
import pl.fhframework.docs.exception.model.DescribedClass;
import pl.fhframework.docs.exception.service.FhDocumentedExceptionService;

import java.util.Set;

/**
 * Created by k.czajkowski on 28.02.2017.
 */
@Getter
@Setter
public class FhDocumentedExceptionModel {

    public static final String FH_EXCEPTIONS = "Fh Exceptions";
    public static final String CONSTRUCTOR_DETAILS = "Constructor Details";
    public static final String CLASS = "Class";
    public static final String CONSTRUCTOR = "Constructor";
    public static final String DECLARATION = "Declaration";
    public static final String DESCRIPTION = "Description";
    public static final String NAME = "Name";
    public static final String PARAMETERS = "Parameters";
    public static final String PACKAGE = "Package";
    public static final String FH_EXCEPTION_DESCRIPTION = "Fh Exceptions. FH specific exceptions extends RuntimeException. Base class in exception hierarchy is FhException";


    @Autowired
    private FhDocumentedExceptionService service;

    private Set<Class> subclasses;

    private Set<DescribedClass> describedExceptions;

    private String fhExceptionsLabel = FH_EXCEPTIONS;
    private String fhExceptions = FH_EXCEPTIONS;
    private String constructorDetailsLabel = CONSTRUCTOR_DETAILS;
    private String classColumnLabel = CLASS;
    private String constructorColumnLabel = CONSTRUCTOR;
    private String declarationColumnLabel = DECLARATION;
    private String descriptionColumnLabel = DESCRIPTION;
    private String parametersColumnLabel = PARAMETERS;
    private String nameColumnLabel = NAME;
    private String packageColumnLabel = PACKAGE;
    private String fhExceptionDescription = FH_EXCEPTION_DESCRIPTION;

    public FhDocumentedExceptionModel(FhDocumentedExceptionService service) {
        //right now we are looking for subclasses of FhException only in pl.fhframework.core directory to save a time
        //if there is a need to look for classes from other packages just add new parameter to findSubclasses() method invocation
        this.subclasses = service.findSubclasses(FhException.class, FhException.class.getPackage().getName());
        this.describedExceptions = service.createDescribedExceptions(this.subclasses);
    }
}
