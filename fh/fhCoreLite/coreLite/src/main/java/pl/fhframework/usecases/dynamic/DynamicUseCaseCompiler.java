package pl.fhframework.usecases.dynamic;

import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.subsystems.Subsystem;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * TODO: Create class cache - if generated date is earlier than process data then get from cache. Two level cache - in class memory (1st level), class files on disk (2nd level)
 * TODO: Create proper package handling - artificial limitations that UC name must be unique
 * TODO: Way of dynamic form components generation should be changed because they may be displayed as static form components
 */
public class DynamicUseCaseCompiler {
    public final static DynamicUseCaseCompiler instance = new DynamicUseCaseCompiler();
    private File baseDir = new File("_dynamic");

    private DynamicUseCaseCompiler() {
        if (!baseDir.exists()) {

                baseDir.mkdir();

        }
    }

    public synchronized <T extends IUseCase> Class<T> createUseCaseClass(UseCaseProcess useCaseProcess, Subsystem subSystem) {
        File javaFile = generateJavaFile(useCaseProcess, subSystem);
        compile(javaFile);
        //Class<?> clazz = loadClass(useCaseProcess.getPackage()+"."+useCaseProcess.getName());
        Class<?> clazz = loadClass(useCaseProcess.getId(), true);

        return (Class<T>) clazz;
    }

    public synchronized Class<?> loadClass(String name, boolean throwException) {
        URLClassLoader classLoader = null;
        try {
            classLoader = URLClassLoader.newInstance(new URL[]{baseDir.toURI().toURL()}, IUseCase.class.getClassLoader());

            Class<?> cls = Class.forName(name, true, classLoader);
            return cls;
        } catch (Exception e) {
            if (throwException) {
                FhLogger.error(e);
                throw new FhException(e);
            }else{
                return null;
            }
        } catch (Throwable throwable){
            throw new FhException("Unable to load class '"+ name +"'");
        }
    }

    private void compile(File javaFile) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        if (!this.getClass().getProtectionDomain().getCodeSource().getLocation().getProtocol().equals("file")){
            //TODO:Hardcoded !!! It should be read from all subsystems and modules!!!!
            String frameworkPath = "C:\\SVN\\Prywatne\\Projekty\\Kazachstan\\eirz\\target\\formsHandler-0.0.1-SNAPSHOT.jar";
            String businessPaths = "C:\\SVN\\Prywatne\\Projekty\\Kazachstan\\eirz\\target\\eirz-0.0.1-SNAPSHOT.jar";
            String classpath = System.getProperty("java.class.path");
            System.setProperty("java.class.path", classpath + ";" + frameworkPath +";"+ businessPaths);

        }
        compiler.run(null, System.out, System.err, javaFile.getPath());
    }

    private File generateJavaFile(UseCaseProcess useCaseProcess, Subsystem subSystem) {
        String javaCode = new JavaCodeGeneratorForDynamicUseCase(useCaseProcess, subSystem).getJavaCode();
        String localFilePath = useCaseProcess.getId().replace('.', '/');
        File javaFile = new File(baseDir, localFilePath + ".java");
        try {
            File directory =  javaFile.getParentFile();
            if (!directory.exists()){
                Path p = Files.createDirectories(directory.toPath());
                if (p!=null) {
                    javaFile.createNewFile();
                }else{
                    throw new FhException("Error in directory creation '"+ directory.getAbsolutePath());
                }
            }
            else if (!javaFile.exists()){
                javaFile.createNewFile();
            }
            Files.write(javaFile.toPath(), javaCode.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            FhLogger.error("Error in saving dynamic class to file '{}'", javaFile.getAbsolutePath(), e);
            throw new FhException(e);
        }
        return javaFile;
    }
}
