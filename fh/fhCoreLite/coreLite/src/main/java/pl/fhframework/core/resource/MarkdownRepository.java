package pl.fhframework.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.*;
import org.eclipse.core.internal.resources.Folder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Scope;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.subsystems.ModuleRegistry;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class MarkdownRepository implements ApplicationListener<ContextRefreshedEvent> {
    private static final Map<String, String> ALLOWED_EXTENSIONS;

    private static final Map<String, List<MarkdownEntry>> MARKDOWN_PER_MODULE = new ConcurrentHashMap<>();

    private static final Map<String, Path> MARKDOWN_DIR_PER_MODULE = new ConcurrentHashMap<>();

    private static final char[] FORBIDDEN_FILE_NAME_CHARS = {'/', '\\', '~', ':'};

    @Value("${usermarkdown.path}")
    private String markdownFilesPath;



    static {
        ALLOWED_EXTENSIONS = new HashMap<>();
        ALLOWED_EXTENSIONS.put("md", MediaType.TEXT_PLAIN_VALUE);
        ALLOWED_EXTENSIONS.put("MD", MediaType.TEXT_PLAIN_VALUE);

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class MarkdownEntry {
        private String name;
        private File file;
        private String type;
        public Boolean directory = false;
        public Boolean parent = false;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            MarkdownEntry that = (MarkdownEntry) o;

            if (name != null ? !name.equals(that.name) : that.name != null) return false;
            //noinspection SimplifiableIfStatement
            if (file != null ? !file.equals(that.file) : that.file != null) return false;
            return type != null ? type.equals(that.type) : that.type == null;
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + (file != null ? file.hashCode() : 0);
            result = 31 * result + (type != null ? type.hashCode() : 0);
            return result;
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent refreshedEvent) {
        Set<Subsystem> subsystems = ModuleRegistry.getLoadedModules();

        for (Subsystem subsystem : subsystems) {
            MARKDOWN_DIR_PER_MODULE.put(subsystem.getName(), getSubsystemFilesPath(subsystem.getName()));
            readSubsystemFiles(subsystem.getName(), null, false);
        }
    }

    public void readSubsystemFiles(String subsystem, String subDir, Boolean moveUp) {
        Path current = MARKDOWN_DIR_PER_MODULE.get(subsystem);
        Path subsystemPath = getSubsystemFilesPath(subsystem);
        Boolean addParent = false;

        if(subDir !=null && !moveUp ){
            current = current.resolve(subDir);
            addParent = true;
        }
        if(current!=null && current.compareTo(subsystemPath ) <= 0 ) {
            moveUp = false;
            addParent = false;
        }
        if(moveUp){
            current = current.getParent();
        }
        if(current!=null && current.compareTo(subsystemPath ) > 0 ) {
            subsystemPath = current;
            addParent = true;
        }


        MARKDOWN_DIR_PER_MODULE.put(subsystem, subsystemPath);


        File subsystemDir = subsystemPath.toFile();
        List<MarkdownEntry> images = new ArrayList<>();
        if (subsystemDir.exists()) {
            if (!subsystemDir.canRead() || !subsystemDir.canWrite()) {
                FhLogger.error("Error while creating subsystem images directory.");
            }

            File[] files = subsystemDir.listFiles();
            for (File file : files) {

                if (file.isDirectory()) {
                    images.add(new MarkdownEntry(file.getName(), file.getAbsoluteFile(), this.getFileTypes(file), true, false));
                }

                if (file.isFile()) {
                    images.add(new MarkdownEntry(file.getName(), file.getAbsoluteFile(), this.getFileTypes(file), false, false));
                }
            }
        } else {
            images = new ArrayList<>();
        }
        if(addParent){
            images.add(new MarkdownEntry("...", subsystemDir, null, true, true));
        }
        images = Collections.synchronizedList(images);
        MARKDOWN_PER_MODULE.put(subsystem, images.stream().sorted(Comparator.comparing(MarkdownEntry::getName)).sorted(Comparator.comparing(MarkdownEntry::getDirectory,Comparator.reverseOrder())).collect(Collectors.toList()));

    }

    public synchronized void addFile(String subsystem, String name, File resource) throws IOException {
        if (!checkFileNameSecure(name)) {
            throw new InvalidImageException("Insecure file name: " + name);
        }
        if (!checkFileExtension(name)) {
            throw new InvalidImageException("Invalid image extension.");
        }

        Path source = Paths.get(resource.toURI());
        Path subsystemImagesPath = MARKDOWN_DIR_PER_MODULE.get(subsystem);;


        // lazy directory creation
        if (!Files.exists(subsystemImagesPath)) {
            try {
                Files.createDirectories(subsystemImagesPath);
            } catch (Exception e) {
                FhLogger.error("Error while creating subsystem images directory.", e);
                return;
            }
        }

        Path destination = subsystemImagesPath.resolve(name);
        Files.copy(source, destination);

        File resultFile = destination.toFile();
        MARKDOWN_PER_MODULE.get(subsystem).add(new MarkdownEntry(name, resultFile, this.getFileTypes(resultFile), false, false));
    }


    public synchronized void addFolder(String subsystem, String name) throws IOException {
        Path subsystemImagesPath = getSubsystemFilesPath(subsystem);
        subsystemImagesPath = subsystemImagesPath.resolve(name);
        // lazy directory creation
        if (!Files.exists(subsystemImagesPath)) {
            try {
                Files.createDirectories(subsystemImagesPath);
                MARKDOWN_PER_MODULE.get(subsystem).add(new MarkdownEntry(name, subsystemImagesPath.toFile(), this.getFileTypes(subsystemImagesPath.toFile()), true, false));
            } catch (Exception e) {
                FhLogger.error("Error while creating subsystem directory.", e);
                return;
            }
        }
    }

    public synchronized void deleteElement(String subsystem, MarkdownEntry selected) throws IOException {
        //Todo add validation is folder empty.
        if (MARKDOWN_PER_MODULE.get(subsystem).contains(selected)) {
            Files.delete(selected.getFile().toPath());
            MARKDOWN_PER_MODULE.get(subsystem).remove(selected);
        }
    }

    public synchronized Optional<File> getFile(String subsystem, String name) {
        Path mainpath = getSubsystemFilesPath(subsystem);
        mainpath = mainpath.resolve(name);
        File file = mainpath.toFile();

        file = (file != null && file.isFile())?file:null;
        return Optional.ofNullable(file);
    }

    public synchronized List<MarkdownEntry> getFiles(String subsystem) {
        return MARKDOWN_PER_MODULE.get(subsystem);
    }

    private boolean checkFileExtension(String fileName) {
        return ALLOWED_EXTENSIONS.keySet().stream().anyMatch(c -> Objects.equals(c, FilenameUtils.getExtension(fileName)));
    }

    private boolean checkFileNameSecure(String fileName) {
        for (char fileNameChar : fileName.toCharArray()) {
            for (char forbiddenChar : FORBIDDEN_FILE_NAME_CHARS) {
                if (fileNameChar == forbiddenChar) {
                    return false;
                }
            }
        }
        return true;
    }

    private Path getSubsystemFilesPath(String subsystem) {
            return new File(ModuleRegistry.getByName(subsystem).getResourcesPath().getFile(), markdownFilesPath).toPath();
    }

    private String getFileSubDirectory(String subsystem, File file) {
        Path dir = getSubsystemFilesPath(subsystem);
        String path = file.getAbsolutePath().replace(dir.toString(), "").replace(file.getName(), "");
        if (path.startsWith(File.separator)) {
            path = path.substring(1);
        }
        if (path.endsWith(File.separator)) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }

    private String getFileTypes(File file) {
        return ALLOWED_EXTENSIONS.get(FilenameUtils.getExtension(file.getName()));
    }

    public Path getRelativePath(String subsystem)  {
        Path current = MARKDOWN_DIR_PER_MODULE.get(subsystem);
        Path subsystemPath = getSubsystemFilesPath(subsystem);
        return  subsystemPath.relativize(current);
    }

}
