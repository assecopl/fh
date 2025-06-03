package pl.fhframework.compiler.core.generator;

import lombok.Getter;
import lombok.Setter;
import org.aspectj.tools.ajc.Main;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.core.FhCL;
import pl.fhframework.core.FhException;
import pl.fhframework.core.FhFormException;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.core.util.StringUtils;
import pl.fhframework.model.forms.Form;

import javax.annotation.PostConstruct;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by Piotr on 2017-02-02.
 */
@Service
public class DynamicClassCompiler {
    private static ThreadLocal<Boolean> coreLiteTarget = new ThreadLocal<>();

    @Value("${fhframework.dynamic.output.directory}")
    private String workDirectory;

    @Getter
    private Path workingDirectoryPath;

    private FhCL workingDirectoryClassloader;

    @Setter
    private boolean classloaderUrlsBasedClassPath = false;

    public DynamicClassCompiler() {
    }

    public DynamicClassCompiler(String workDirectory) throws MalformedURLException {
        this.workDirectory = workDirectory;
        init();
    }

    public DynamicClassCompiler(Path workingDirectoryPath, FhCL workingDirectoryClassloader) {
        this.workingDirectoryPath = workingDirectoryPath;
        this.workingDirectoryClassloader = workingDirectoryClassloader;
        // not init needed
    }

    @PostConstruct
    private void init() throws MalformedURLException {
        // ensure working directory exists
        workingDirectoryPath = Paths.get(workDirectory).toAbsolutePath();
        if (Files.exists(workingDirectoryPath)) {
            if (!Files.isDirectory(workingDirectoryPath)) {
                throw new FhFormException(workingDirectoryPath + " is not a directory");
            }
        } else {
            try {
                Files.createDirectories(workingDirectoryPath);
            } catch (IOException e) {
                throw new FhFormException("Cannot create " + workingDirectoryPath, e);
            }
        }

        // create classloader
        URL url = workingDirectoryPath.toUri().toURL();

        workingDirectoryClassloader = FhCL.classLoader;
        addURLToClassLoader(url, workingDirectoryClassloader);
    }


    public static void addURLToClassLoader(URL url, FhCL classLoader) {
        if (!Arrays.asList(classLoader.getURLs()).contains(url)) {
            try {
                classLoader.addURL(url);
            } catch (Throwable t) {
                throw new FhException(String.format("Error when adding url to system ClassLoader. Add {%s} to class path.", url.toString()), t);
            }
        }
    }

    public Path createDynamicJavaFile(String generatedCode, String newClassPackage, String newClassName) {
        return createDynamicJavaFile(workingDirectoryPath, generatedCode, newClassPackage, newClassName);
    }

    public Path createDynamicJavaFile(Path otherBasePath, String generatedCode, String newClassPackage, String newClassName) {
        return createDynamicFile(otherBasePath, generatedCode, newClassPackage, newClassName, ".java");
    }

    public Path createDynamicFile(String generatedCode, String newClassPackage, String newClassName, String extension) {
        return createDynamicFile(workingDirectoryPath, generatedCode, newClassPackage, newClassName, extension);
    }

    public Path createDynamicFile(Path otherBasePath, String generatedCode, String newClassPackage, String newClassName, String extension) {
        // prepare package directory if not exists
        Path packagePath;
        try {
            packagePath = otherBasePath.resolve(newClassPackage.replace(".", "/"));
            Files.createDirectories(packagePath);
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot create %s package directory under %s", newClassPackage, otherBasePath.toString()), e);
        }

        // create java file
        Path javaFilePath = packagePath.resolve(newClassName + extension);
        String javaFilePathString = javaFilePath.toAbsolutePath().toString();
        try {
            Files.write(javaFilePath, generatedCode.getBytes(Charset.forName("UTF-8")));
            return javaFilePath;
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot create form java file %s", javaFilePathString), e);
        }
    }

    public String readDynamicFile(String newClassPackage, String newClassName, String extension) {
    return  readDynamicFile(workingDirectoryPath, newClassPackage, newClassName, extension);
    }

    public String readDynamicFile(Path otherBasePath, String newClassPackage, String newClassName, String extension) {
        // prepare package directory
        Path packagePath = otherBasePath.resolve(newClassPackage.replace(".", "/"));
        if (!packagePath.toFile().exists()) {
            throw new RuntimeException(String.format("%s directory doesn't exist", packagePath.toString()));
        }

        // read file
        Path filePath = packagePath.resolve(newClassName + extension);
        String filePathString = filePath.toAbsolutePath().toString();
        try {
            byte[] readBytes = Files.readAllBytes(filePath);
            return new String(readBytes, Charset.forName("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(String.format("Cannot read file %s", filePathString), e);
        }
    }

    public void compile(Collection<Path> javaFiles, boolean isAspectWeavingNeeded) {
        String fileListString = javaFiles.toString();

        long startTime = System.currentTimeMillis();
        FhLogger.info(this.getClass(), "Starting compilation of {}", fileListString);

        if (isAspectWeavingNeeded) {
            List<String> compilerArgs = new ArrayList<>();
            compilerArgs.add("-encoding");
            compilerArgs.add("utf8");
            compilerArgs.add("-d");
            compilerArgs.add(workingDirectoryPath.toAbsolutePath().toString());
            compilerArgs.add("-classpath");
            String classPath;
            if (classloaderUrlsBasedClassPath) {
                classPath = getClassPathAsString();
            } else {
                classPath = System.getProperties().getProperty("java.class.path") + File.pathSeparator +
                        workingDirectoryPath.toAbsolutePath().toString();
            }
            String libPath = getLibsPath();
            if (!StringUtils.isNullOrEmpty(libPath)) {
                classPath = classPath.concat(libPath);
            }
            compilerArgs.add(classPath);
            compilerArgs.add("-aspectpath");
            compilerArgs.add(getAspectJarPath(classPath));
            compilerArgs.add("-source");
            compilerArgs.add("1.8");
            compilerArgs.add("-showWeaveInfo");
            javaFiles.forEach(singleFile -> compilerArgs.add(singleFile.toAbsolutePath().toString()));

            List<String> failMessages = new LinkedList<>();
            Main.bareMain(compilerArgs.toArray(new String[compilerArgs.size()]), false, failMessages, failMessages, null, null);
            if (failMessages.size() > 0) {
                String errResult = String.join("\n", failMessages);
                FhLogger.error("Errors while compiling files {}:\n{}\n", fileListString, errResult);
                throw new FhException("Compilation of " + javaFiles.size() + " files failed:\n" + errResult + "\n");
            }
        }
        else {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            if (compiler == null) {
                throw new FhFormException("Application must be run from JDK not JRE");
            }

            // run compiler
            ByteArrayOutputStream stdOut = new ByteArrayOutputStream(1000);
            ByteArrayOutputStream stdErr = new ByteArrayOutputStream(1000);

			String classPath = getClassPathAsString();

            List<String> compilerArgs = new ArrayList<>();
            compilerArgs.add("-encoding");
            compilerArgs.add("utf8");
            compilerArgs.add("-d");
            compilerArgs.add(workingDirectoryPath.toAbsolutePath().toString());
            compilerArgs.add("-cp");
            compilerArgs.add((!classPath.isEmpty() ? classPath + File.pathSeparator : "")
                    + System.getProperties().getProperty("java.class.path")
                    + File.pathSeparator + workingDirectoryPath.toAbsolutePath().toString());

			javaFiles.forEach(singleFile -> compilerArgs.add(singleFile.toAbsolutePath().toString()));
            int result = compiler.run(null, stdOut, stdErr, compilerArgs.toArray(new String[compilerArgs.size()]));

            String outResult = new String(stdOut.toByteArray(), Charset.forName("UTF-8"));
            String errResult = new String(stdErr.toByteArray(), Charset.forName("UTF-8"));

            if (!outResult.isEmpty()) {
                FhLogger.debug(this.getClass(), logger -> logger.log("Output while compiling form files:\n{}\n", outResult));
            }

            if (result > 0) {
                FhLogger.error("Errors while compiling files {}:\n{}\n", fileListString, stdErr);
                throw new RuntimeException("Compilation of " + javaFiles.size() + " files failed:\n" + stdErr + "\n");
            } else if (!errResult.isEmpty()) {
                FhLogger.warn("Warnings while successfully compiling form files {}:\n{}\n", fileListString, errResult);
            }
        }

        FhLogger.info(this.getClass(), "Compilation of {} succeeded. Total time: {} ms", fileListString, System.currentTimeMillis() - startTime);
    }

    public Class<?> loadDynamicClass(String packageName, String className) {
        try {
            long startTime = System.currentTimeMillis();
            Class<?> loadedClass = workingDirectoryClassloader.loadClass(packageName + "." + className);
            FhLogger.info(this.getClass(), "Class {} loaded in {} ms", loadedClass.getName(), System.currentTimeMillis() - startTime);
            return (Class<? extends Form>) loadedClass;
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(String.format("Error loading compiled form class %s.%s from %s",
                    packageName, className, workingDirectoryPath.toString()), e);
        }
    }

    public Class<?> classForName(String fullClassName, boolean required) {
        try {
            return Class.forName(fullClassName, true, workingDirectoryClassloader);
        } catch (ClassNotFoundException e) {
            if (required) {
                throw new RuntimeException(String.format("Error resolving compiled class %s from %s",
                        fullClassName, workingDirectoryPath.toString()), e);
            } else {
                return null;
            }
        }
    }

    private List<File> getClassPathMaven() {
        List<File> classPathEntries = new ArrayList<>();
        try {
            Enumeration<URL> packages = FhCL.classLoader.getResources(".");
            for (; packages.hasMoreElements(); ) {
                FhResource url = FhResource.get(packages.nextElement());

                String realFilePath = url.getURL().getFile();
                if (realFilePath.contains("/WEB-INF/classes")) {
                    classPathEntries.add(url.getFile());
                }
                if (realFilePath.endsWith(".jar")
                        || realFilePath.endsWith(".jar/")) {
                    classPathEntries.addAll(Arrays.asList(url.getFile().getParentFile().listFiles()));
                }
            }
        } catch (IOException ex){
            throw new FhException("Creating classpath failed", ex);
        }
        return classPathEntries;
    }

    private List<File> getClassPath() {
        List<File> classPathEntries = new ArrayList<>();
        try {
            Enumeration<URL> packages = FhCL.classLoader.getResources(".");
            for (; packages.hasMoreElements(); ) {
                FhResource url = FhResource.get(packages.nextElement());

                String realFilePath = url.getURL().getFile();
                if (realFilePath.contains("/WEB-INF/classes")) {
                    classPathEntries.add(url.getFile());
                }
                if (realFilePath.endsWith(".jar")
                        || realFilePath.endsWith(".jar/")) {
                    classPathEntries.addAll(Arrays.asList(url.getFile().getParentFile().listFiles()));
                }
            }
        } catch (IOException ex){
            throw new FhException("Creating classpath failed", ex);
        }
        return classPathEntries;
    }

    private String getClassPathAsString() {
        if (classloaderUrlsBasedClassPath) {
            return Arrays.stream(FhCL.classLoader.getURLs())
                    .filter(url -> FileUtils.getFile(url).isPresent())
                    .map(url -> {
                        Optional<Path> path = FileUtils.getFile(url);
                        return path.isPresent() ? path.get().toAbsolutePath().toString() : null;
                    })
                    .filter(file -> file != null)
                    .collect(Collectors.joining(File.pathSeparator));
        } else {
            return getClassPath().stream()
                    .map(res -> res.toString())
                    .collect(Collectors.joining(File.pathSeparator));
        }
    }

    private String getAspectJarPath(String classPath) {
        String jarClassPath = Arrays.stream(classPath.split(File.pathSeparator)).filter(jar -> jar.contains("fhAspects")).findAny().orElse(null);
        if (jarClassPath == null) {
            File basePath = new File(FhCL.classLoader.getURLs()[0].getFile()).getParentFile();
            File libPath = new File (basePath, "lib");
            File jarClassFile = Arrays.stream(libPath.listFiles()).filter(file -> file.getName().contains("fhAspects")).findAny().orElse(null);
            if (jarClassFile != null) {
                return jarClassFile.getAbsolutePath();
            }
        }

        return jarClassPath;
    }

    private String getLibsPath() {
        try {
            File basePath = new File(FhCL.classLoader.getURLs()[0].toURI()).getParentFile();
            File libPath = new File(basePath, "lib");

            StringBuilder paths = new StringBuilder();

            if (libPath.exists()) {
                Arrays.stream(libPath.listFiles()).filter(File::isFile).forEach(file -> {
                    paths.append(File.pathSeparator);
                    paths.append(file.getAbsolutePath());
                });
            }

            return paths.toString();
        } catch (URISyntaxException e) {
            throw new FhException("Lib path unavailable for AspectJ compiler", e);
        }
    }

    public static boolean isCoreLiteTarget() {
        return coreLiteTarget.get() == Boolean.TRUE;
    }

    public static void setCoreLiteTarget(Boolean coreLite) {
        coreLiteTarget.set(coreLite);
    }
}
