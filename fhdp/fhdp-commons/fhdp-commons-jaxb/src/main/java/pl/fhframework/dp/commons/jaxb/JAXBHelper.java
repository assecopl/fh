package pl.fhframework.dp.commons.jaxb;

import org.slf4j.Logger;
import org.w3c.dom.Node;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Reader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Source and target classess should be JAXB annotated.
 *
 * @author <a href="mailto:dariusz_skrudlik@javiko.pl">Dariusz Skrudlik</a>
 * @version $Revision: 3016 $, $Date: 2019-08-16 16:08:41 +0200 (pt., 16 sie
 * 2019) $
 * @created 09.05.2017
 */
public class JAXBHelper {

    private static Logger log = org.slf4j.LoggerFactory.getLogger(JAXBHelper.class);
    private static Map<String, JAXBContext> contextStore = new ConcurrentHashMap<String, JAXBContext>();

    private static JAXBContext getContextInstance(Class<?>... classes) throws JAXBException {
        String key = "clazz";
        for (Class<?> clazz : classes) {
            key += "_";
            key += clazz.getName();
        }
        JAXBContext context = contextStore.get(key);
        if (context == null) {
            context = JAXBContext.newInstance(classes);
            log.debug("New jaxbcontext created for class: " + key);
            contextStore.put(key, context);
            return context;
        }
        return context;
    }

    private static JAXBContext getContextInstance(String contextPath) throws JAXBException {
        String key = "path_" + contextPath;
        JAXBContext context = contextStore.get(key);
        if (context == null) {
            log.debug("New jaxbcontext created for path: " + key);
            context = JAXBContext.newInstance(contextPath);
            contextStore.put(key, context);
            return context;
        }
        return context;
    }

    public static byte[] marshal(Class clazz, Object object) {
        return marshal(clazz,object,null);
    }


    public static byte[] marshal(Class clazz, Object object, Map<String, Object> properties) {
        try {
            return marshal(getContextInstance(clazz), object, properties);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] marshal(String classpaths, Object object) {
        return marshal(classpaths,object,null);
    }


    public static byte[] marshal(String classpaths, Object object, Map<String, Object> properties) {
        try {
            return marshal(getContextInstance(classpaths), object, properties );
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] marshal(JAXBContext jaxbContext, Object object, Map<String, Object> properties) {
        byte[] result = null;
        try {
            Marshaller marshaller = jaxbContext.createMarshaller();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            if(properties != null) {
                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    marshaller.setProperty(entry.getKey(), entry.getValue());
                }
            }
            marshaller.marshal(object, outputStream);
            result = outputStream.toByteArray();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static byte[] marshal(Object object, Class... clazz) {
        byte[] result = null;
        try {
            JAXBContext jaxbContext = getContextInstance(clazz);
            Marshaller marshaller = jaxbContext.createMarshaller();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            marshaller.marshal(object, outputStream);
            result = outputStream.toByteArray();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }

        return result;
    }

    public static <T> T unmarshal(Class<T> clazz, byte[] data) {
        try {
            return (T) getContextInstance(clazz).createUnmarshaller().unmarshal(new ByteArrayInputStream(data));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T unmarshal(Class<T> clazz, Node content ) {
        try {
            return (T) getContextInstance(clazz).createUnmarshaller().unmarshal(content);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static Object unmarshal(String classPath, byte[] data ) {
        try {
            return  getContextInstance(classPath).createUnmarshaller().unmarshal(new ByteArrayInputStream(data));
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

//    public static <T> T unmarshal(Class<T> clazz, StringReader reader) {
//        try {
//            return (T) getContextInstance(clazz).createUnmarshaller().unmarshal(reader);
//        } catch (JAXBException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static  <T> T unmarshal(Class<T> clazz, Reader reader) {
        try {
            return (T) getContextInstance(clazz).createUnmarshaller().unmarshal(reader);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static  <T> T unmarshal(Class<T> clazz, Source source) {
        try {
            return (T) getContextInstance(clazz).createUnmarshaller().unmarshal(source);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T  unmarshal(Class<T> clazz, ByteArrayInputStream inputStream) {
        try {
            return (T) getContextInstance(clazz).createUnmarshaller().unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> T  unmarshal(String packageName, Node node, Class<T> declaredClass) {
        try {
            return (T )getContextInstance(packageName).createUnmarshaller().unmarshal(node,declaredClass).getValue();
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }
}
