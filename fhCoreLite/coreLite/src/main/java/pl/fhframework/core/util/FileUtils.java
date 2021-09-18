package pl.fhframework.core.util;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.VfsResource;
import org.springframework.util.ResourceUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.io.FhResource;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.JarFile;

/**
 * Created by Piotr on 2017-02-02.
 */
public class FileUtils {

    public static Instant getLastModified(String fullFilePath) {
        return getLastModified(Paths.get(fullFilePath));
    }

    public static Instant getLastModified(Path fullFilePath) {
        try {
            BasicFileAttributes xmlFileAttrs = Files.readAttributes(fullFilePath, BasicFileAttributes.class);
            return xmlFileAttrs.lastModifiedTime().toInstant().truncatedTo(ChronoUnit.MILLIS);
        } catch (IOException e) {
            throw new RuntimeException("Cannot check last modification time of " + fullFilePath.toString(), e);
        }
    }
    public static Instant getLastModified(FhResource resource) {
        return Instant.ofEpochMilli(resource.lastModified());
    }
    public static Instant getLastModified(URL url) {
        if ("file".equals(url.getProtocol())) {
            return getLastModified(getFile(url).get());
        } else if ("jar".equals(url.getProtocol())) {
            try {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                try (JarFile jarFile = connection.getJarFile()) { // try-with-resources to close the jar file
                    return Instant.ofEpochMilli(jarFile.getEntry(connection.getEntryName()).getTime());
                }
            } catch (IOException e) {
                throw new RuntimeException("Exception while checking modification timestamp of: " + url.toString(), e);
            }
        } else if (Objects.equals(ResourceUtils.URL_PROTOCOL_VFS, url.getProtocol()) ) {
            try {
                VfsResource vfsResource = new VfsResource(url.openConnection().getContent());
                return Instant.ofEpochMilli(vfsResource.getFile().lastModified());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(" Instant.ofEpochMilli(jarFile.getJarEntry(vfsResource.getFilename()).getTime()); ");
            }
        }

        else {
            throw new RuntimeException("Not supported URL protocol:" + url.getProtocol());
        }
    }

    /*public static byte[] getContent(FhResource resource) {
        try {
            return IOUtils.toByteArray(resource.getInputStream());
        } catch (IOException e) {
            throw new FhException("An I/O error occured "
                    + resource!=null ? resource.getDescription() : "" , e);
        }
    }*/

   /* public static byte[] getContent(URL url) {
        try {
              if ("file".equals(url.getProtocol())) {
                return Files.readAllBytes(ResourceUtils.getFile(url).toPath());
            } else if ("jar".equals(url.getProtocol())) {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                try (JarFile jarFile = connection.getJarFile();
                     InputStream input = jarFile.getInputStream(jarFile.getEntry(connection.getEntryName()))) {
                    return getAllBytes(input);
                }
            } else if (Objects.equals(ResourceUtils.URL_PROTOCOL_VFS, url.getProtocol()) ) {
                VfsResource vfsResource = new VfsResource(url.openConnection().getContent());
                return Files.readAllBytes(vfsResource.getFile().toPath());
            } else {
                throw new RuntimeException("Not supported URL protocol:" + url.getProtocol());
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading: " + url, e);
        }
    }*/

    /**
     * Tries to get simple path to url.
     * @param url url
     * @return simple path if URL was a plain file/directory
     */
    public static Optional<Path> getFile(URL url) {
        if (url.getProtocol().equals("file")) {
                return Optional.of(FhResource.get(url).getExternalPath());
        } else if (Objects.equals(ResourceUtils.URL_PROTOCOL_VFS, url.getProtocol()) ) {
            try {
                VfsResource vfsResource = new VfsResource(url.openConnection().getContent());
                return Optional.of(vfsResource.getFile().toPath());
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        return Optional.empty();
    }

    public static URL getURL(String filePath) {
        return getURL(Paths.get(filePath));
    }

    public static URL getURL(Path filePath) {
        try {
            return filePath.toAbsolutePath().toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] getAllBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[4096];
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    public static URL resolve(FhResource baseResource, String path) {
        return resolve(baseResource.getURL(), path);
    }

    public static URL resolve(URL baseUrl, String path) {
        try {
            path = path.replace('\\', '/');
            String urlString = baseUrl.toExternalForm();
            if (!urlString.endsWith("/") && !path.startsWith("/")) {
                urlString += '/';
            }
            urlString += path;
            return new URL(urlString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot resolve " + path + " against " + baseUrl, e);
        }
    }

    public static boolean exists(URL url) {
        try {
            if ("file".equals(url.getProtocol())) {
                return Files.exists(getFile(url).get());
            } else if ("jar".equals(url.getProtocol())) {
                JarURLConnection connection = (JarURLConnection) url.openConnection();
                try (JarFile jarFile = connection.getJarFile()) {
                     return jarFile.getEntry(connection.getEntryName()) != null;
                }
            } else if (Objects.equals(ResourceUtils.URL_PROTOCOL_VFS, url.getProtocol()) ) {
                VfsResource vfsResource = new VfsResource(url.openConnection().getContent());
                return vfsResource.exists();
            } else {
                throw new RuntimeException("Not supported URL protocol:" + url.getProtocol());
            }
        } catch (IOException e) {
            throw new RuntimeException("Exception while reading: " + url, e);
        }
    }

    public static FhResource getParent(FhResource resource) {
        return FhResource.get(getParent(resource.getURL()));
    }

    public static URL getParent(URL url) {
        String urlString = url.getFile();
        String suffix = "";
        if (urlString.endsWith("/")) {
            suffix = "/";
            urlString = urlString.substring(0, urlString.length() - 1);
        }

        List<String> parts = Arrays.asList(urlString.split("/"));
        if (parts.size() < 2) {
            throw new RuntimeException("URL has no parent: " + url);
        }

        // get parent elements
        parts = CollectionsUtils.getWithoutLast(parts);

        try {
            String resultString = url.getProtocol() + ":" + StringUtils.joinWithEmpty(parts, "/") + suffix;
            if (url.getProtocol().equals("jar") && resultString.indexOf('!') == resultString.length() - 1) {
                resultString += "/";
            }
            return new URL(resultString);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Cannot get parent: " + url, e);
        }
    }

    public static URL asURL(Path path){
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new FhException("Malformed Path "
                    + path!=null ? path.toString() : "" , e);
        }
    }

    public static URI asURI(Path path){
        return path.toUri();
    }

    public static FhResource asResource(Path path){
        return FhResource.get(new PathResource(path));
    }

    public static FhResource asResource(URL url){
        return FhResource.get(url);
    }

    public static String unixCompatilbePath(String path) {
        return path.replaceAll("\\\\", "/");
    }
}
