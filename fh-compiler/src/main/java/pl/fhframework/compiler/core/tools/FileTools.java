package pl.fhframework.compiler.core.tools;

import pl.fhframework.core.FhException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Common tools for file accessing
 */
public class FileTools {
    public static List<Path> getFilesInPath(List<Path>rootPaths, Predicate<? super Path> filter){
        List<Path> resultPaths = new ArrayList<>();
        try {
            for (Path path : rootPaths) {
                Files.walk(path).filter(Files::isRegularFile).filter(filter).forEach(resultPaths::add);
            }
        } catch (IOException e) {
            throw new FhException(e);
        }
        return resultPaths;
    }


}
