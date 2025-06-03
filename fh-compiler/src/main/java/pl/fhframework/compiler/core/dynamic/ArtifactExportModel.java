package pl.fhframework.compiler.core.dynamic;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.subsystems.Subsystem;

import java.io.File;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import static pl.fhframework.compiler.core.util.ZipUtils.EXPORT_IMAGES_CATALOG;
import static pl.fhframework.compiler.core.util.ZipUtils.EXPORT_MARKDOWN_CATALOG;

@Getter
@Setter
public class ArtifactExportModel {
    private boolean containsStaticDependencies = false;
    private Set<DynamicClassFileDescriptor> descriptors = new HashSet<>();
    private Set<ImageDescriptor> images = new HashSet<>();
    private Set<MarkdownDescriptor> markdownFiles = new HashSet<>();
    private Subsystem subsystem;

    @Getter
    @Setter
    public static class ImageDescriptor {
        private String filename;
        private Subsystem subsystem;

        ImageDescriptor(String filename, Subsystem subsystem) {
            this.filename = filename;
            this.subsystem = subsystem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageDescriptor that = (ImageDescriptor) o;
            return filename.equals(that.filename) &&
                    subsystem.equals(that.subsystem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filename, subsystem);
        }

        public String getPath() {
            return EXPORT_IMAGES_CATALOG + File.separator + getSubsystem().getName() + File.separator + getFilename();
        }
    }

    @Getter
    @Setter
    public static class MarkdownDescriptor {
        private String filename;
        private Subsystem subsystem;

        MarkdownDescriptor(String filename, Subsystem subsystem) {
            this.filename = filename;
            this.subsystem = subsystem;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ImageDescriptor that = (ImageDescriptor) o;
            return filename.equals(that.filename) &&
                    subsystem.equals(that.subsystem);
        }

        @Override
        public int hashCode() {
            return Objects.hash(filename, subsystem);
        }

        public String getPath() {
            return EXPORT_MARKDOWN_CATALOG + File.separator + getSubsystem().getName() + File.separator + getFilename();
        }
    }
}
