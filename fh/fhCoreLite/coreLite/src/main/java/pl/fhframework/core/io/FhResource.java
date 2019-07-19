package pl.fhframework.core.io;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.VfsResource;
import org.springframework.util.ResourceUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.util.FhVfsUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;
import java.util.Objects;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import static org.springframework.util.ResourceUtils.*;

/**
 * Created by krzysztof.kobylarek on 2017-05-26.
 */
public class
FhResource implements Resource {

    public enum Type {
        FILE, JAR, VFS
    }

    private Resource resource;

    private Type resourceType = null;

    private FhResource(){}

    private FhResource(Resource resource, Type type){
        this.resource = resource;
        this.resourceType = type;
    }

    @Override
    public boolean exists() {
        return resource.exists();
    }

    @Override
    public boolean isReadable() {
        return resource.isReadable();
    }

    @Override
    public boolean isOpen() {
        return resource.isOpen();
    }

    @Override
    public URL getURL() {
        try {
            return resource.getURL();
        } catch (IOException e) {
            throw new FhException("Malformed URL "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    @Override
    public URI getURI() {
        try {
            return resource.getURI();
        } catch (IOException e) {
            throw new FhException("Malformed URL "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    @Override
    public File getFile() {
        try {
            return resource.getFile();
        } catch (IOException e) {
            throw new FhException("Malformed URL "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    @Override
    public long contentLength() {
        try {
            return resource.contentLength();
        } catch (IOException e) {
            throw new FhException("Resource cannot be resolved "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    @Override
    public long lastModified() {
        try {
            return resource.lastModified();
        } catch (IOException e) {
            throw new FhException("Resource cannot be resolved "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    @Override
    public FhResource createRelative(String relativePath) {
        try {
            return FhResource.get(resource.createRelative(relativePath));
        } catch (IOException e) {
            throw new FhException("Resource cannot be determined "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    @Override
    public String getFilename() {
        return resource.getFilename();
    }

    @Override
    public String getDescription() {
        return resource.getDescription();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return resource.getInputStream();
        } catch (IOException e) {
            throw new FhException("the stream could not be opened "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    public Path getExternalPath(){
        if (Type.JAR==getResourceType()){
            try {
                JarURLConnection jarURLConnection = ((JarURLConnection) getURL().openConnection());
                File expandPath = null;
                File jarFileInfo =  new File(jarURLConnection.getJarFileURL().getPath());
                if (jarFileInfo.isFile()){
                    expandPath = new File(jarFileInfo.getParentFile()+java.io.File.separator+jarFileInfo.getName()+"."+"contents");
                    if (!expandPath.exists()){
                        if (expandPath.mkdir()){

                            JarFile jarFile = jarURLConnection.getJarFile();
                            JarEntry fileFromJar = null;
                            Enumeration<JarEntry> jarEntries = jarFile.entries();
                            while(jarEntries.hasMoreElements()){
                                fileFromJar = jarEntries.nextElement();
                                File f = new File(expandPath + java.io.File.separator + fileFromJar.getName());
                                if (fileFromJar.isDirectory()){
                                    f.mkdirs();
                                } else {
                                    FileUtils.writeByteArrayToFile(f,
                                            IOUtils.toByteArray(jarURLConnection.getJarFile().getInputStream(fileFromJar)));
                                }
                                if (fileFromJar.getLastModifiedTime() != null) {
                                    f.setLastModified(fileFromJar.getLastModifiedTime().toMillis());
                                }
                            }
                        }
                    }
                    return expandPath.toPath().resolve(jarURLConnection.getJarEntry().getName());
                }
            } catch (IOException e) {
                throw new FhException("I/O exception occurs"
                        + resource!=null ? resource.getDescription() : "" , e);
            }
        }

        return getFile().toPath();
    }

    public FhResource toExternalResource(){
        return FhResource.get(getExternalPath());
    }

    public String toExternalPath(){
        return getFile().getAbsolutePath();
    }

    public FhResource resolve(String path) {
        return get(getFile().toPath().resolve(path));
    }

    public FhResource resolve(URI resource) {
        return get(getURI().resolve(resource));
    }

    private static Type whatIsType(Resource resource)  {
        try {
            return whatType(resource.getURL());
        } catch (IOException e) {
            throw new FhException("Malformed URL "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }

    public static FhResource get(String resourcePath){
        //return new FhResource(new FileSystemResource(resourcePath));
        return new FhResource(new PathResource(resourcePath), Type.FILE);
    }

    public static FhResource get(Resource resource){
        return new FhResource(resource, whatIsType(resource));
    }

    public static FhResource get(Path path){
        return new FhResource(new PathResource(path), Type.FILE);
    }

    public static FhResource get(URL url){
        FhResource resource = null;
        if (Objects.equals(ResourceUtils.URL_PROTOCOL_VFS, url.getProtocol()) ) {
            try {
                VfsResource vfsResource = new VfsResource(url.openConnection().getContent());
                resource = new FhResource(vfsResource, Type.VFS);
            } catch (IOException e) {
                throw new FhException("I/O exception occurs"
                        + resource!=null ? resource.getDescription() : "" , e);
            }
        } else {
            resource = new FhResource(new UrlResource(url), whatType(url));
        }
        return resource;
    }

    public static FhResource get(URI uri) {
        try {
            return new FhResource(new UrlResource(uri), whatType(uri.toURL()));
        }
        catch (MalformedURLException ex){
            throw new FhException("the stream could not be opened "
                    + uri!=null ? uri.toString() : "" , ex);
        }
    }

    public byte[] getContent(){
        URL url = getURL();
        try {
            if (Objects.equals(URL_PROTOCOL_FILE, url.getProtocol())) {
                return Files.readAllBytes(ResourceUtils.getFile(url).toPath());
            } else if (Objects.equals(URL_PROTOCOL_JAR, url.getProtocol())) {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                try (JarFile jarFile = connection.getJarFile();
                     InputStream input = new BufferedInputStream(
                             jarFile.getInputStream(jarFile.getEntry(connection.getEntryName())))) {
                    return IOUtils.toByteArray(input);
                }
            } else if (Objects.equals(ResourceUtils.URL_PROTOCOL_VFS, url.getProtocol()) ) {
                Object content = url.openConnection().getContent();
                if (content instanceof InputStream) {
                    return IOUtils.toByteArray((InputStream) content);
                } else {
                    VfsResource vfsResource = new VfsResource(content);
                    return Files.readAllBytes(vfsResource.getFile().toPath());
                }
            } else {
                throw new RuntimeException("Not supported URL protocol:" + url.getProtocol());
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading: " + url, e);
        }
    }

    public Type getResourceType() {
        return resourceType;
    }

    public void walkFileAndTree(FileVisitor<Path> fileVisitor){
        URL url = getURL();
        URI uri = getURI();
        if (Objects.equals(URL_PROTOCOL_FILE, url.getProtocol())) {
            try {
                Files.walkFileTree(Paths.get(uri), fileVisitor);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (Objects.equals(URL_PROTOCOL_VFS, url.getProtocol())) {
            try {
                FhVfsUtils.walkVfsTree(uri, fileVisitor, true);
            } catch (Exception e) {
                throw new FhException("Processing failed:"
                        + resource!=null ? resource.getDescription() : "" , e);
            }
        }
    }

    private static Type whatType(URL url) {
        String protocol = url.getProtocol();
        Type type = null;
        if (Objects.equals(URL_PROTOCOL_JAR, protocol)){
            type = Type.JAR;
        }
        if (Objects.equals(URL_PROTOCOL_FILE, protocol)){
            type = Type.FILE;
        }
        if (Objects.equals(URL_PROTOCOL_VFS, protocol)){
            type = Type.VFS;
        }
        return type;
    }

    @Override
    public String toString() {
        return resourceType + ": " + resource.getDescription();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FhResource that = (FhResource) o;

        if (!resource.equals(that.resource)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return resource.hashCode();
    }
}
