package pl.fhframework.compiler.forms;

import org.springframework.util.ReflectionUtils;
import pl.fhframework.core.io.FhResource;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by User on 2017-05-31.
 */
public class CustomDelegatingFileManger implements JavaFileManager {
    JavaFileManager delegated;
    ClassLoader delegatedClassLoader;

    Map<Location, Map<String, List<CustomJavaFileObject>>> globalClasses = new HashMap<>();

    CustomDelegatingFileManger(JavaFileManager fileManager, ClassLoader classLoader){
        this.delegated=fileManager;
        this.delegatedClassLoader=classLoader;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return delegatedClassLoader;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        for (JavaFileObject.Kind kind : kinds) {
           /* if (JavaFileObject.Kind.CLASS != kind) {
                int x=1;
            }
*/
            Iterable<JavaFileObject> list = delegated.list(location, packageName, kinds, recurse);
            long size = list.spliterator().getExactSizeIfKnown();
            if (size > 0)
                return list;

            if (JavaFileObject.Kind.CLASS != kind)
                break;
            Field classesField = ReflectionUtils.findField(ClassLoader.class, "classes");
            classesField.setAccessible(true);
            List classes = (List) ReflectionUtils.getField(classesField, delegatedClassLoader);
            Object filteredClassses = classes.stream()
                    .filter(_class -> ((Class) _class).getName().startsWith(packageName))
                    .collect(Collectors.toList());

        /*for (Object obj : classes){
            Class c = (Class)obj;
            if (c.getCanonicalName()
        }
*/
            List<CustomJavaFileObject> listOfClasses = new ArrayList<>();
            for (Object obj : (List) filteredClassses) {
                Class c = (Class) obj;
                if (c.getName().startsWith(packageName)) {

                    CustomJavaFileObject javaFile = null;
                    javaFile = new CustomJavaFileObject(packageName, c.getSimpleName(),
                            Paths.get(".").toUri(), JavaFileObject.Kind.CLASS);
                    listOfClasses.add(javaFile);
                }

            }
            if (listOfClasses.size() > 0) {
                return new ArrayList<>(listOfClasses);
            }

            globalClasses.putIfAbsent(location, new HashMap<>());
            Map<String, List<CustomJavaFileObject>> mapPackageToClasses = globalClasses.get(location);

            if (!mapPackageToClasses.containsKey(packageName)) {
                mapPackageToClasses.putIfAbsent(packageName, new ArrayList<>());
                listOfClasses = mapPackageToClasses.get(packageName);
                Enumeration<URL> packages = delegatedClassLoader.getResources(packageName.replace('.', '/'));
                for (; packages.hasMoreElements(); ) {
                    FhResource url = FhResource.get(packages.nextElement());
                    List<Path> classFiles = Files.list(url.getExternalPath())
                            .filter(path -> path.toFile().isFile()).collect(Collectors.toList());

                    for (Path classFile : classFiles) {
                        String className = classFile.getFileName().toString().replace(".class", "");
                        CustomJavaFileObject fileObject =
                                new CustomJavaFileObject(packageName, className, classFile.toUri(), JavaFileObject.Kind.CLASS);
                        listOfClasses.add(fileObject);
                    }
                }

            } else {
                listOfClasses = mapPackageToClasses.getOrDefault(packageName, new ArrayList<>());
            }
            return new ArrayList<>(listOfClasses);
        }
        return new ArrayList<>();
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        String binaryName = null;
        /*if (file instanceof CustomJavaFileObject) {
            CustomJavaFileObject fileCustom = (CustomJavaFileObject)file;
            if (globalClasses.containsKey(location)) {
                Map<String, List<CustomJavaFileObject>>  mapPackageToClasses = globalClasses.get(location);
                if (mapPackageToClasses.containsKey(fileCustom.getPackageName())) {
                    List<CustomJavaFileObject> classes = mapPackageToClasses.get(fileCustom.getPackageName());
                    if (classes.contains(file)){
                        Optional<CustomJavaFileObject> classFile =
                            classes.stream().filter(
                                cls ->
                                    Objects.equals(cls.getPackageName(), fileCustom.getPackageName())
                                            &&
                                            Objects.equals(cls.getClassName(), fileCustom.getClassName())
                            ).findAny();
                        if (classFile.isPresent()){
                            binaryName=classFile.get().getPackageName()+"."+classFile.get().getClassName();
                        }
                    }
                }
            }
        }*/

        if (file instanceof CustomJavaFileObject) {
            CustomJavaFileObject fileCustom = (CustomJavaFileObject)file;
            binaryName = fileCustom.binaryName();
        }

        return binaryName;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return delegated.isSameFile(a,b);
        /*if (a instanceof CustomJavaFileObject
                && b instanceof CustomJavaFileObject){
            CustomJavaFileObject aCustom = (CustomJavaFileObject)a;
            CustomJavaFileObject bCustom = (CustomJavaFileObject)b;
            return  Objects.equals(aCustom.binaryName(), bCustom.binaryName());
        }
        return false;*/
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return delegated.handleOption(current, remaining);
    }

    @Override
    public boolean hasLocation(Location location) {
        return globalClasses.containsKey(location);
        //return delegated.hasLocation(location);
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return delegated.getJavaFileForInput(location, className, kind);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        return delegated.getJavaFileForOutput(location, className, kind, sibling);
    }

    @Override
    public FileObject getFileForInput(Location location, String packageName, String relativeName) throws IOException {
        return delegated.getFileForInput(location, packageName, relativeName);
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return delegated.getFileForOutput(location, packageName, relativeName, sibling);
    }

    @Override
    public void flush() throws IOException {
        delegated.flush();
    }

    @Override
    public void close() throws IOException {
        delegated.close();
    }

    @Override
    public int isSupportedOption(String option) {
        return delegated.isSupportedOption(option);
    }
}
