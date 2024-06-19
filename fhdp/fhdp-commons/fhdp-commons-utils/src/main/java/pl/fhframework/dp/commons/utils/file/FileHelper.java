package pl.fhframework.dp.commons.utils.file;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.dp.commons.utils.xml.ParserHelper;

import java.io.*;
import java.net.URL;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Slf4j
public class FileHelper {


    public static String getHumanReadableFileSize(long bytes) {
        long absB = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absB < 1024) {
            return bytes + " B";
        }
        long value = absB;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absB > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.1f %cB", value / 1024.0, ci.current());
    }

    public static void saveXml(String filename, String xml) {

        FileOutputStream fos = null;
        try {
            File f = new File(filename);
            DirectoryHelper.checkDirectory(f.getParentFile());

            fos = new FileOutputStream(f, false);
            fos.write(xml.getBytes(ParserHelper.parseEncoding(xml)));
            fos.flush();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Error saving file: " + filename, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

    public static String readFile(String filename) {
        return readFile(filename, System.getProperty("file.encoding"));
    }

    public static String readFile(String filename, String encoding) {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(filename);
            return readInputStream(fis, encoding);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }

    }

    public static byte[] shrinkNullBytes(byte[] bytes) {
        byte result[] = null;
        if (bytes != null && bytes.length > 0) {
            int l = bytes.length;
            while (l > 0 && bytes[l - 1] == 0) {
                l--;
            }
            if (l != bytes.length) {
                result = Arrays.copyOf(bytes, l);
            }
        } else {
            result = bytes;
        }
        return result;
    }

    public static byte[] readBytesFromStream(InputStream in) {
        ByteArrayOutputStream data = new ByteArrayOutputStream();
        try {
            byte[] buffer = new byte[8192];
            int count = 0;
            while ((count = in.read(buffer)) >= 0) {
                data.write(buffer, 0, count);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        return data.toByteArray();
    }

    public static byte[] readInputStreamtoBytes(InputStream instrm) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            BufferedInputStream bufInputStream = new BufferedInputStream(instrm);
            while (bufInputStream.available() > 0) {
                byte[] data = new byte[bufInputStream.available()];
                bufInputStream.read(data);
                buffer.write(data);
            }
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        } finally {
            try {
                instrm.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
        return buffer.toByteArray();
    }

    public static byte[] readInputStreamtoBytes(InputStream instrm, String encoding) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        InputStreamReader isr = null;

        Reader reader = null;
        try {
            if (encoding != null) {
                isr = new InputStreamReader(instrm, encoding);
            } else {
                isr = new InputStreamReader(instrm);
            }
            reader = new BufferedReader(isr);
            int ch;
            while ((ch = reader.read()) > -1) {
                buffer.write(ch);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
        return buffer.toByteArray();
    }

    public static String readInputStream(InputStream instrm, String encoding) {
        StringBuilder buffer = new StringBuilder();
        InputStreamReader isr = null;
        Reader reader = null;
        try {
            if (encoding != null) {
                isr = new InputStreamReader(instrm, encoding);
            } else {
                isr = new InputStreamReader(instrm);
            }

            reader = new BufferedReader(isr);
            int ch;
            while ((ch = reader.read()) > -1) {
                buffer.append((char) ch);
            }

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }

        return buffer.toString();
    }

    public static void writeFile(String filename, byte[] content) {
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            fos.write(content);
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Error saving file: " + filename, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }
    }

    public static void writeFile(String filename, String str) {
        writeFile(filename, str, System.getProperty("file.encoding"));
    }

    public static void writeFile(String filename, String str, String encoding) {

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
            Writer out = new OutputStreamWriter(fos, encoding);
            out.write(str);
            out.close();
        } catch (IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new RuntimeException("Error saving file: " + filename, ex);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                    //ignore
                }
            }
        }

    }

    /**
     * Loads the given properties file and returns a Properties object containing the
     * key,value pairs in that file.
     * The properties files should be in the class path.
     */
    public static Properties loadProperties(String propertiesName) throws IOException {
        Properties bundle = null;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        URL url = loader.getResource(propertiesName);
        if (url == null) {
            throw new IOException("Properties file " + propertiesName + " not found");
        }

        log.trace("Properties file=" + url);

        InputStream is = url.openStream();
        if (is != null) {
            bundle = new Properties();
            bundle.load(is);
        } else {
            throw new IOException("Properties file " + propertiesName + " not available");
        }
        return bundle;
    }

    public static InputStream getInputStreamFromJar(String fileNameInsideJar) throws IOException {
        URL url = Thread.currentThread().getContextClassLoader().getResource(fileNameInsideJar);
        if (url != null) {
            return url.openStream();
        }

        return null;
    }

    /**
     * @param fileName
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(String fileName) throws IOException {
        File myFile = new File(fileName);
        InputStream is = new FileInputStream(myFile);

        // Get the size of the file
        long length = myFile.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + myFile.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * Retrieves byte array from File object.
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static byte[] getBytesFromFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        // Get the size of the file
        long length = file.length();

        // You cannot create an array using a long type.
        // It needs to be an int type.
        // Before converting to an int type, check
        // to ensure that file is not larger than Integer.MAX_VALUE.
        if (length > Integer.MAX_VALUE) {
            // File is too large
        }

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        // Ensure all the bytes have been read in
        if (offset < bytes.length) {
            throw new IOException("Could not completely read file " + file.getName());
        }

        // Close the input stream and return bytes
        is.close();
        return bytes;
    }

    /**
     * @param fileName
     * @param bytesToSave
     * @throws IOException
     */
    public static void setBytesToFile(String fileName, byte[] bytesToSave) throws IOException {
        // Open or create the output file
        File myFile = new File(fileName);
        FileOutputStream os = new FileOutputStream(myFile);
        os.write(bytesToSave);
        os.flush();
        os.close();
    }

    /**
     * Stores content of byte array to file represented as File object.
     *
     * @param file
     * @param bytesToSave
     * @throws IOException
     */
    public static void setBytesToFile(File file, byte[] bytesToSave) throws IOException {
        FileOutputStream os = new FileOutputStream(file);
        os.write(bytesToSave);
        os.flush();
        os.close();
    }

    public static final String preparePath(String path) {
        if (path == null || StringUtils.isEmpty(path)) {
            path = "/";
        } else {
            path = path.trim();
            if (!path.startsWith("/")) {
                path = "/" + path;
            }
            if (!path.endsWith("/")) {
                path = path + "/";
            }
        }
        return path;
    }

    public static final byte[] getBytesFromJar(String zipFilePath, String zipEntryName) {

        ZipInputStream zipIn = null;
        try {
            zipIn = new ZipInputStream(new FileInputStream(zipFilePath));
            ZipEntry entry = zipIn.getNextEntry();
            while (entry != null) {
                if (zipEntryName.equals(entry.getName())) {

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    byte[] bytesIn = new byte[4096];
                    int read = 0;
                    while ((read = zipIn.read(bytesIn)) != -1) {
                        bos.write(bytesIn, 0, read);
                    }
                    return bos.toByteArray();
                }
                zipIn.closeEntry();
                entry = zipIn.getNextEntry();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                zipIn.closeEntry();
            } catch (IOException e) {
                //ignore
            }
            try {
                zipIn.close();
            } catch (IOException e) {
                //ignore
            }
        }


        return null;
    }
}
