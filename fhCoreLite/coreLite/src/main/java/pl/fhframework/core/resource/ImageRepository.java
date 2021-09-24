package pl.fhframework.core.resource;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class ImageRepository implements ApplicationListener<ContextRefreshedEvent> {
    private static final Map<String, String> ALLOWED_EXTENSIONS;

    private static final Map<String, List<ImageEntry>> IMAGES_PER_MODULE = new ConcurrentHashMap<>();

    private static final char[] FORBIDDEN_FILE_NAME_CHARS = { '/', '\\', '~', ':' };

    @Value("${userimages.path}")
    private String userimagesPath;

    static {
        ALLOWED_EXTENSIONS = new HashMap<>();
        ALLOWED_EXTENSIONS.put("png", MediaType.IMAGE_PNG_VALUE);
        ALLOWED_EXTENSIONS.put("PNG", MediaType.IMAGE_PNG_VALUE);
        ALLOWED_EXTENSIONS.put("jpg", MediaType.IMAGE_JPEG_VALUE);
        ALLOWED_EXTENSIONS.put("JPG", MediaType.IMAGE_JPEG_VALUE);
        ALLOWED_EXTENSIONS.put("jpeg", MediaType.IMAGE_JPEG_VALUE);
        ALLOWED_EXTENSIONS.put("JPEG", MediaType.IMAGE_JPEG_VALUE);
        ALLOWED_EXTENSIONS.put("md", MediaType.TEXT_PLAIN_VALUE);
        ALLOWED_EXTENSIONS.put("MD", MediaType.TEXT_PLAIN_VALUE);

    }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class ImageEntry {
        private String name;
        private File file;
        private String type;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ImageEntry that = (ImageEntry) o;

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
            readSubsystemImages(subsystem.getName());
        }
    }

    private void readSubsystemImages(String subsystem) {
        File subsystemPath = getSubsystemImagesPath(subsystem).toFile();
        List<ImageEntry> images;
        if (subsystemPath.exists()) {
            if (!subsystemPath.canRead() || !subsystemPath.canWrite()) {
                FhLogger.error("Error while creating subsystem images directory.");
            }

            Collection<File> files = org.apache.commons.io.FileUtils.listFiles(subsystemPath, new IOFileFilter() {
                @Override
                public boolean accept(File file) {
                    return true;
                }

                @Override
                public boolean accept(File file, String name) {
                    return checkFileExtension(name);
                }
            }, null);

            images = files.stream().map(file -> new ImageEntry(file.getName(), file.getAbsoluteFile(), this.getImageType(file))).collect(Collectors.toList());
        } else {
            images = new ArrayList<>();
        }
        IMAGES_PER_MODULE.put(subsystem, Collections.synchronizedList(images));
    }

    public synchronized void addImage(String subsystem, String name, File resource) throws IOException {
        if (!checkFileNameSecure(name)) {
            throw new InvalidImageException("Insecure file name: " + name);
        }
        if (!checkFileExtension(name)) {
            throw new InvalidImageException("Invalid image extension.");
        }

        Path source = Paths.get(resource.toURI());
        Path subsystemImagesPath = getSubsystemImagesPath(subsystem);

        // lazy directory creation
        if (!Files.exists(subsystemImagesPath)) {
            try {
                Files.createDirectories(subsystemImagesPath);
            } catch(Exception e) {
                FhLogger.error("Error while creating subsystem images directory.", e);
                return;
            }
        }

        Path destination = subsystemImagesPath.resolve(name);
        Files.copy(source, destination);

        File resultFile = destination.toFile();
        ImageEntry newImageEntry = new ImageEntry(name, resultFile, this.getImageType(resultFile));
        IMAGES_PER_MODULE.get(subsystem).add(0, newImageEntry);
    }

    public synchronized void deleteImage(String subsystem, ImageEntry selected) throws IOException {
        if (IMAGES_PER_MODULE.get(subsystem).contains(selected)) {
            Files.delete(selected.getFile().toPath());
            IMAGES_PER_MODULE.get(subsystem).remove(selected);
        }
    }

    public synchronized Optional<ImageEntry> getImage(String subsystem, String name) {
        return IMAGES_PER_MODULE.get(subsystem).stream().filter(c -> c.name.equals(name)).findFirst();
    }

    public synchronized List<ImageEntry> getImages(String subsystem) {
        return IMAGES_PER_MODULE.get(subsystem);
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

    private Path getSubsystemImagesPath(String subsystem) {
        return new File(ModuleRegistry.getByName(subsystem).getResourcesPath().getFile(), userimagesPath).toPath();
    }

    private String getImageType(File file) {
        return ALLOWED_EXTENSIONS.get(FilenameUtils.getExtension(file.getName()));
    }
}
