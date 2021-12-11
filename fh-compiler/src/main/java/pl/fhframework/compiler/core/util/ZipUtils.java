package pl.fhframework.compiler.core.util;

import org.springframework.core.io.Resource;
import org.springframework.util.FileCopyUtils;
import pl.fhframework.compiler.core.model.ZipFileMetadata;
import pl.fhframework.compiler.core.model.ZipImageFileMetadata;
import pl.fhframework.compiler.core.model.ZipMarkdownFileMetadata;
import pl.fhframework.compiler.core.model.ZipMetadata;
import pl.fhframework.compiler.core.dynamic.ArtifactExportModel;
import pl.fhframework.compiler.core.dynamic.DynamicClassFileDescriptor;
import pl.fhframework.core.FhException;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.resource.ImageRepository;
import pl.fhframework.core.resource.MarkdownRepository;
import pl.fhframework.core.util.FileUtils;
import pl.fhframework.subsystems.Subsystem;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    private static final String FILE_ENCODING = "UTF-8";
    public static final String METADATA_FILE = "Dynamic_Class_Metadata.xml";
    public static final String ZIP_EXTENSION = ".zip";
    public static final String EXPORT_IMAGES_CATALOG = "_images";
    public static final String EXPORT_MARKDOWN_CATALOG = "_markdown";
    private static final String TMP_DIR = System.getProperty("java.io.tmpdir");

    public static URL createFromClassesList(ArtifactExportModel model, String name) {
        return createFromClassesList(model, name, null, null);
    }

    public static URL createFromClassesList(ArtifactExportModel model, String name, ImageRepository imageRepository, MarkdownRepository markdownRepository) {
        Path path = Paths.get(TMP_DIR, name + ZIP_EXTENSION);

        try (ZipOutputStream out = new ZipOutputStream(Files.newOutputStream(path))) {
            out.putNextEntry(new ZipEntry(FileUtils.unixCompatilbePath(METADATA_FILE)));
            StringWriter writer = createZipMetadata(model);
            out.write(writer.toString().getBytes(FILE_ENCODING));
            for (DynamicClassFileDescriptor cc : model.getDescriptors()) {

                ZipEntry entry = new ZipEntry(FileUtils.unixCompatilbePath(cc.getRelativePath()));

                out.putNextEntry(entry);
                out.write(cc.getResource().getContent());
            }

            if (imageRepository != null) {
                for (ArtifactExportModel.ImageDescriptor imageDescriptor : model.getImages()) {
                    Optional<ImageRepository.ImageEntry> imageEntryOptional = imageRepository.getImage(imageDescriptor.getSubsystem().getName(), imageDescriptor.getFilename());

                    if (imageEntryOptional.isPresent()) {
                        ImageRepository.ImageEntry imageEntry = imageEntryOptional.get();

                        ZipEntry entry = new ZipEntry(FileUtils.unixCompatilbePath(EXPORT_IMAGES_CATALOG + File.separator + imageDescriptor.getSubsystem().getName() + File.separator + imageEntry.getName()));

                        out.putNextEntry(entry);
                        out.write(Files.readAllBytes(Paths.get(imageEntry.getFile().getAbsolutePath())));
                    }
                }
            }

            if (markdownRepository != null) {
                for (ArtifactExportModel.MarkdownDescriptor markdownDescriptor : model.getMarkdownFiles()) {
                    Optional<File> markdownEntryOptional = markdownRepository.getFile(markdownDescriptor.getSubsystem().getName(), markdownDescriptor.getFilename());

                    if (markdownEntryOptional.isPresent()) {
                        File markdownFile = markdownEntryOptional.get();

                        ZipEntry entry = new ZipEntry(FileUtils.unixCompatilbePath(EXPORT_MARKDOWN_CATALOG + File.separator + markdownDescriptor.getSubsystem().getName() + File.separator + markdownFile.getName()));

                        out.putNextEntry(entry);
                        out.write(Files.readAllBytes(Paths.get(markdownFile.getAbsolutePath())));
                    }
                }
            }

            out.closeEntry();
        } catch (IOException e) {
            FhLogger.error("Error while creating export zip file", e);
        }

        return FileUtils.getURL(path);
    }

    /**
     * Extract files info without saving to disk.
     *
     * @param zipFile Zip formatted file.
     * @return List of files.
     */
    public static List<File> extractFilesInfoFromZip(Resource zipFile) {
        List<File> result = new ArrayList<>();
        try {
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(zipFile.getInputStream());
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String fileName = FileUtils.unixCompatilbePath(ze.getName());
                File newFile = new File(fileName);
                result.add(newFile);
                ze = zis.getNextEntry();

            }
            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            throw new FhException(ex);
        }
        return result;
    }

    public static Optional<File> getFileFromZip(Resource zipFile, String path) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            ZipFile zf = new ZipFile(zipFile.getFile());
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {

                String a = FileUtils.unixCompatilbePath(path);
                String zipFilePath = FileUtils.unixCompatilbePath(ze.getName());
                if (zipFilePath.equals(a)) {
                    File tempFile = File.createTempFile("import-", ".tmp");

                    InputStream is = zf.getInputStream(ze);
                    org.apache.commons.io.FileUtils.copyInputStreamToFile(is, tempFile);
                    return Optional.of(tempFile);
                }

                ze = zis.getNextEntry();
            }
        }

        return Optional.empty();
    }

    public static Optional<File> getFileFromZip(Resource zipFile, String fileName, Path outputDirectory) throws IOException {
        try (ZipInputStream zis = new ZipInputStream(zipFile.getInputStream())) {
            File directory = outputDirectory.toFile();
            if (!directory.exists()) {
                directory.mkdirs();
            }

            ZipFile zf = new ZipFile(zipFile.getFile());
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {

                String uxFileName = FileUtils.unixCompatilbePath(fileName);
                String zipFilePath = FileUtils.unixCompatilbePath(ze.getName());
                if (zipFilePath.equals(uxFileName)) {
                    File tempFile = new File(directory, uxFileName);

                    InputStream is = zf.getInputStream(ze);
                    org.apache.commons.io.FileUtils.copyInputStreamToFile(is, tempFile);
                    return Optional.of(tempFile);
                }

                ze = zis.getNextEntry();
            }
        }

        return Optional.empty();
    }

    public static ZipMetadata extractMetadataFromZip(Resource zipFile) throws IOException, JAXBException {
        ZipMetadata result = null;
        ZipInputStream zis = new ZipInputStream(zipFile.getInputStream());
        ZipEntry ze = zis.getNextEntry();
        while (ze != null) {
            String filePath = FileUtils.unixCompatilbePath(ze.getName());
            if (filePath.equals(METADATA_FILE)) {
                JAXBContext context = JAXBContext.newInstance(ZipMetadata.class);
                Unmarshaller unMarshaller = context.createUnmarshaller();
                result = (ZipMetadata) unMarshaller.unmarshal(zis);
                break;
            }
            ze = zis.getNextEntry();

        }
        return result;
    }

    public static Path copyFileToRepository(File file, String fileNewPath, Subsystem subsystem) {
        Path fullPath = null;
        try {
            fullPath = subsystem.getBasePath().resolve(fileNewPath).getExternalPath();
            File newFile = fullPath.toFile();
            new File(newFile.getParent()).mkdirs();
            FileCopyUtils.copy(file, newFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (fullPath != null) {
            return fullPath;
        } else throw new RuntimeException("Exception while copying file to repository.");
    }


    /**
     * Extract and save
     *
     * @param zipFile
     * @param fileOldPath
     * @param subsystem
     */
    public static Path copyZipFileToRepository(Resource zipFile, String fileOldPath, Subsystem subsystem, boolean override) {
        byte[] buffer = new byte[1024];
        Path fullPath = null;
        try {
            //get the zip file content
            ZipInputStream zis = new ZipInputStream(zipFile.getInputStream());
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();
            while (ze != null) {
                String filePath = FileUtils.unixCompatilbePath(ze.getName());
                if (filePath.equals(METADATA_FILE)) {
                    ze = zis.getNextEntry();
                    continue;
                }
                File file = new File(filePath);
                if (file.getPath().equals(fileOldPath)) {
                    ;
                    fullPath = subsystem.getBasePath().resolve(fileOldPath).getExternalPath();
                    File newFile = fullPath.toFile();
                    new File(newFile.getParent()).mkdirs();
                    FileOutputStream fos;
                    if (override) {
                        fos = new FileOutputStream(newFile, false);
                    } else {
                        fos = new FileOutputStream(newFile);
                    }
                    int len;
                    while ((len = zis.read(buffer)) > 0) {
                        fos.write(buffer, 0, len);
                    }

                    fos.close();
                }
                ze = zis.getNextEntry();

            }
            zis.closeEntry();
            zis.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        if (fullPath != null) {
            return fullPath;
        } else throw new RuntimeException("Exception while copying file to repository.");
    }

    private static StringWriter createZipMetadata(ArtifactExportModel exportModel) {
        List<ZipFileMetadata> zipFileMetadataList = new ArrayList<>();
        StringWriter result;

        for (DynamicClassFileDescriptor descriptor : exportModel.getDescriptors()) {
            ZipFileMetadata fileMetadata = new ZipFileMetadata();
            // Replacing file separators with slash. It's necessary for Metadata info to process export/import between different OS applications.
            fileMetadata.setFilePath(descriptor.getRelativePath().replace(File.separator, "/"));
            fileMetadata.setFileSubsystemId(descriptor.getSubsystem().getName());
            zipFileMetadataList.add(fileMetadata);
        }

        List<ZipImageFileMetadata> zipImageFileMetadata = new ArrayList<>();
        for (ArtifactExportModel.ImageDescriptor imageDescriptor : exportModel.getImages()) {
            ZipImageFileMetadata metadata = new ZipImageFileMetadata();

            metadata.setFilePath(imageDescriptor.getPath().replace(File.separator, "/"));
            metadata.setFileSubsystemId(imageDescriptor.getSubsystem().getName());
            zipImageFileMetadata.add(metadata);
        }

        List<ZipMarkdownFileMetadata> zipMarkdownFileMetadata = new ArrayList<>();
        for (ArtifactExportModel.MarkdownDescriptor markdownDescriptor : exportModel.getMarkdownFiles()) {
            ZipMarkdownFileMetadata metadata = new ZipMarkdownFileMetadata();

            metadata.setFilePath(markdownDescriptor.getPath().replace(File.separator, "/"));
            metadata.setFileSubsystemId(markdownDescriptor.getSubsystem().getName());
            zipMarkdownFileMetadata.add(metadata);
        }

        ZipMetadata zipMetadata = new ZipMetadata();
        zipMetadata.setZipFileMetadataList(zipFileMetadataList);
        zipMetadata.setZipImageFileMetadata(zipImageFileMetadata);
        zipMetadata.setZipMarkdownFileMetadata(zipMarkdownFileMetadata);

        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(ZipMetadata.class);
            Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            result = new StringWriter();
            jaxbMarshaller.marshal(zipMetadata, result);
        } catch (JAXBException e) {
            throw new FhException(e);
        }

        return result;
    }

    public static boolean checkZipFormat(File file) {
        try (
                //get the zip file content
                ZipFile zf = new ZipFile(file);
        ) {
            Enumeration<? extends ZipEntry> entries = zf.entries();
            while (entries.hasMoreElements()) {
                entries.nextElement();
            }
        } catch (Exception e) {
            return false;
        }

        return true;
    }
}
