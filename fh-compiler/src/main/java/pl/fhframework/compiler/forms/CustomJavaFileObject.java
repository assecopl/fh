package pl.fhframework.compiler.forms;

import javax.tools.SimpleJavaFileObject;
import java.net.URI;

/**
 * Created by User on 2017-06-01.
 */
public class CustomJavaFileObject extends SimpleJavaFileObject {
    /**
     * Construct a SimpleJavaFileObject of the given kind and with the
     * given URI.
     *
     * @param uri  the URI for this file object
     * @param kind the kind of this file object
     */
    protected CustomJavaFileObject(String packageName, String className, URI uri, Kind kind) {
        super(uri, kind);
        this.packageName=packageName;
        this.className=className;
    }

    private String packageName;
    private String className;

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String binaryName() {
        return packageName+"."+className;
    }

    @Override
    public String getName() {
        return binaryName();
    }
}
