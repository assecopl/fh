package pl.fhframework.core.util;

import org.springframework.core.io.VfsUtils;
import org.springframework.util.ReflectionUtils;
import pl.fhframework.core.FhException;
import pl.fhframework.core.io.FhResource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileVisitor;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * JBoss VFS filesystem helpers
 *
 * Created by krzysztof.kobylarek on 2017-05-30.
 */
public class FhVfsUtils extends VfsUtils {
    private static final String jboss_vfs_class = "org.jboss.vfs.VFS";
    private static final String jboss_virtualFile_class = "org.jboss.vfs.VirtualFile";

    private static Class<?> vfsClass = null;
    private static Class<?> virtualFileClass = null;

    private static String GET_CHILDREN = "getChildren";
    private static String VF_GET_PHYSICAL_FILE = "getPhysicalFile";
    private static String VF_TO_URL = "toURL";
    private static String VF_TO_URI = "toURI";

    private static Map<String, Method> vfsMethods = new HashMap<>();
    private static Map<String, Method> virtualFileMethods = new HashMap<>();

    static {
        try {
            vfsClass = FhVfsUtils.class.getClassLoader().loadClass(jboss_vfs_class);
            virtualFileClass = FhVfsUtils.class.getClassLoader().loadClass(jboss_virtualFile_class);

            vfsMethods.put(GET_CHILDREN, ReflectionUtils.findMethod(vfsClass, GET_CHILDREN));
            virtualFileMethods.put(GET_CHILDREN, ReflectionUtils.findMethod(virtualFileClass, GET_CHILDREN));
            virtualFileMethods.put(VF_GET_PHYSICAL_FILE, ReflectionUtils.findMethod(virtualFileClass, VF_GET_PHYSICAL_FILE));
            virtualFileMethods.put(VF_TO_URL, ReflectionUtils.findMethod(virtualFileClass, VF_TO_URL));
            virtualFileMethods.put(VF_TO_URI, ReflectionUtils.findMethod(virtualFileClass, VF_TO_URI));
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Could not detect JBoss VFS infrastructure", ex);
        }
    }

    // --- public methods area ---

    public static void walkVfsTree(URI uri, FileVisitor<Path> fileVisitor, boolean recursive) throws Exception{
        Object vsfResource = VfsUtils.getRoot(uri.toURL()); // getFile
        List<Object> vsfResourceChildren = _getVF_Children(vsfResource);
        for (Object virtualFile : vsfResourceChildren){
            File f = null;
            try {
                f = vfsdirect_getPhysicalFile(virtualFile);
                if (f.isDirectory()) {
                    fileVisitor.preVisitDirectory(f.toPath(), null);
                    fileVisitor.visitFile(f.toPath(), null);
                    if (recursive)
                        walkVfsTree(vfsdirect_toURI(virtualFile), fileVisitor, recursive);
                    fileVisitor.postVisitDirectory(f.toPath(), null);
                } else {
                    fileVisitor.visitFile(f.toPath(), null);
                }
            } catch (IOException ex){
                if (f != null)
                    fileVisitor.visitFileFailed(f.toPath(), ex);
            }
        }
    }

    public static void walkVfsTree2(URI uri, FileVisitor<Path> fileVisitor, boolean recursive) throws Exception{
        Object vsfResource = VfsUtils.getRoot(uri.toURL()); // getFile
        List<FhResource> vsfResourceChildren = getVF_Children(vfsdirect_toURL(vsfResource));
        for (FhResource virtualFile : vsfResourceChildren){
            File file = virtualFile.getFile();
            try {
                if (file.isDirectory()) {
                    fileVisitor.preVisitDirectory(file.toPath(), null);
                    fileVisitor.visitFile(file.toPath(), null);
                    if (recursive)
                        walkVfsTree(virtualFile.getURI(), fileVisitor, recursive);
                    fileVisitor.postVisitDirectory(file.toPath(), null);
                } else {
                    fileVisitor.visitFile(file.toPath(), null);
                }
            } catch (IOException ex){
                if (file != null)
                    fileVisitor.visitFileFailed(file.toPath(), ex);
            }
        }
    }

    public static List<FhResource> getVF_Children(FhResource vfsResource) throws IOException {
        return getVF_Children(vfsResource.getURL());
    }

    public static List<FhResource> getVF_Children(URL vfsUrl) throws IOException {
        checkIfCorrectType(vfsUrl);
        List<Object> virtualFilesList = (List<Object>) invokeVfsMethod(virtualFileMethods.get(GET_CHILDREN), VfsUtils.getRoot(vfsUrl));
        List<FhResource> fhResourceList = new ArrayList<>();
        for (Object virtualFile : virtualFilesList){
            File file = vfsdirect_getPhysicalFile(virtualFile);
            fhResourceList.add(FhResource.get(file.toPath()));
        }
        return fhResourceList;
    }

    public static File getPhysicalFile(FhResource vfsResource) throws IOException {
        checkIfCorrectType(vfsResource);
        return vfsdirect_getPhysicalFile(VfsUtils.getRoot(vfsResource.getURL()));
        //return (File) invokeVfsMethod(virtualFileMethods.get(VF_GET_PHYSICAL_FILE), VfsUtils.getRoot(vfsResource.getURL()));
    }

    public static URL toURL(FhResource virtualFile) throws IOException{
        checkIfCorrectType(virtualFile);
        return vfsdirect_toURL(VfsUtils.getRoot(virtualFile.getURL()));
        //return (URL) invokeVfsMethod(virtualFileMethods.get(VF_TO_URL), VfsUtils.getRoot(virtualFile.getURL()));
    }

    public static URI toURI(FhResource virtualFile) throws IOException{
        checkIfCorrectType(virtualFile);
        return vfsdirect_toURI(VfsUtils.getRoot(virtualFile.getURL()));
        //return (URI) invokeVfsMethod(virtualFileMethods.get(VF_TO_URI), VfsUtils.getRoot(virtualFile.getURL()));
    }


    // --- private area, do not enter :]

    private static void checkIfCorrectType(URL vfsUrl){
        checkIfCorrectType(FhResource.get(vfsUrl));
    }

    private static void checkIfCorrectType(FhResource vfsResource){
        if (FhResource.Type.VFS != vfsResource.getResourceType())
            throw new FhException("Unsupported protocol: " + vfsResource.getDescription() );
    }

    // VFS static method
    public static List<Object> getVFS_Children() throws IOException {
        return (List<Object>) invokeVfsMethod(vfsMethods.get(GET_CHILDREN), null);
    }

    // invoked on VirtualFile instance
    private static List<Object> _getVF_Children(Object vfsResource) throws IOException {
        return (List<Object>) invokeVfsMethod(virtualFileMethods.get(GET_CHILDREN), vfsResource);
    }

    private static File vfsdirect_getPhysicalFile(Object vfsResource) throws IOException {
        return (File) invokeVfsMethod(virtualFileMethods.get(VF_GET_PHYSICAL_FILE), vfsResource);
    }

    public static URL vfsdirect_toURL(Object virtualFile) throws IOException{
        return (URL) invokeVfsMethod(virtualFileMethods.get(VF_TO_URL), virtualFile);
    }

    public static URI vfsdirect_toURI(Object virtualFile) throws IOException{
        return (URI) invokeVfsMethod(virtualFileMethods.get(VF_TO_URI), virtualFile);
    }
}
