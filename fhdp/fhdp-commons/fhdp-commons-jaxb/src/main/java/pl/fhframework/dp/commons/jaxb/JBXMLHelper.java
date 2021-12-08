package pl.fhframework.dp.commons.jaxb;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ConcurrentHashMap;

public abstract class JBXMLHelper {

    private static Logger logger = LoggerFactory.getLogger(JBXMLHelper.class);
    private static ConcurrentHashMap<Object, JAXBContext> class2Jaxb = new ConcurrentHashMap<Object, JAXBContext>();

    private static SAXParserFactory spf = XXETools.getXXESafeSAXParserFactory();

    public static Object fromXMLString(Class<?> c, String xml) throws JAXBException {

        Source xmlSource = null;
        synchronized (spf) {
            try {
                spf.setNamespaceAware(true);
                xmlSource = new SAXSource(spf.newSAXParser().getXMLReader(), new InputSource(new StringReader(xml)));
            } catch (SAXException | ParserConfigurationException e) {
                throw new JAXBException("Converted from SAXException to JAXBException", e);
            }
        }
		try {
			JAXBContext context = class2Jaxb.get(c);
			if (context == null) {
				context = JAXBContext.newInstance(c);
				class2Jaxb.put(c, context);
			}

			Unmarshaller unmarshaller = context.createUnmarshaller();

			Object unmarshal = unmarshaller.unmarshal(xmlSource);

			if (unmarshal instanceof JAXBElement<?>) {
				JAXBElement<?> jaxbElement = (JAXBElement<?>) unmarshal;
				return jaxbElement.getValue();
			} else {
				return unmarshal;
			}
		} catch (javax.xml.bind.JAXBException e) {
			throw new JAXBException(e);
		}
    }

    public static String toXMLString(Object o) throws JAXBException {
        return toXMLString(o, false);
    }

    public static String toXMLStringPretty(Object o) throws JAXBException {
        return toXMLString(o, true);
    }

    private static String toXMLString(Object o, boolean pretty) throws JAXBException {
    	try {
			String xmlResult = "";
			JAXBContext context = class2Jaxb.get(o.getClass());
			if (context == null) {
				context = JAXBContext.newInstance(o.getClass());
				class2Jaxb.put(o.getClass(), context);
			}
			Marshaller marshaller = context.createMarshaller();
			if (pretty) {
				marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			}
			StringWriter sw = new StringWriter();
			// Write to System.out
			marshaller.marshal(o, sw);
			xmlResult = sw.toString();
			//if (SysProps.On.equals(SysProps.getLC("JBXML.cutNamespaces"))) {
			//!UCDEV! lepsza metoda obcinania nadmiarowych namespace'ów
			xmlResult = RawMessageHelper.cutUnusedNamespaces(xmlResult);
			//	}
			return xmlResult;
		} catch (javax.xml.bind.JAXBException e) {
			throw new JAXBException(e);
		}
    }

    public static String toXMLString(JAXBElement<?> jaxbElement, Class<?> clazz) throws JAXBException {
        try {
            String xmlResult = null;
            JAXBContext context = JAXBContext.newInstance(clazz);
            Marshaller marshaller = context.createMarshaller();
            StringWriter sw = new StringWriter();
            marshaller.marshal(jaxbElement, sw);
            xmlResult = sw.toString();
            //	if (SysProps.On.equals(SysProps.getLC("JBXML.cutNamespaces"))) {
            //!UCDEV! lepsza metoda obcinania nadmiarowych namespace'ów
            xmlResult = RawMessageHelper.cutUnusedNamespaces(xmlResult);
            //	}
            return xmlResult;
        } catch (javax.xml.bind.JAXBException e) {
            throw new JAXBException(e);
        }
    }

    public static Object fromNonRootXMLString(Class<?> clazz, String xml) throws JAXBException {
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            return ((JAXBElement<?>) unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), clazz)).getValue();
        } catch (javax.xml.bind.JAXBException e) {
            throw new JAXBException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringFromNonRootObject(Class<?> clazz, Object o) throws JAXBException {
        return toXMLStringFromNonRootObject(clazz, o, "uri", "local");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringFromNonRootObject(Object o) throws JAXBException {
        return toXMLStringFromNonRootObject(o != null ? o.getClass() : null, o);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringFromNonRootObject(Class<?> clazz, Object o, String rootName) throws JAXBException {
        return toXMLStringFromNonRootObject(clazz, o, rootName, null);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringFromNonRootObject(Class<?> clazz, Object o, String rootName, String namespaceURI) throws JAXBException {
        return toXMLStringFromNonRootObject(clazz, o, true, rootName, namespaceURI, null);
    }

    public static String toXMLStringFromNonRootObject(Class<?> clazz, Object o, boolean pretty, String rootName, String namespaceURI, String prefix) throws JAXBException {
        return toXMLStringFromNonRootObject(o, pretty, rootName, namespaceURI, prefix, clazz);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringFromNonRootObject(Object o, boolean pretty, String rootName, String namespaceURI, String prefix, Class<?>... clazzList)
            throws JAXBException {
        Class clazz = clazzList[0];
        if (clazz == null) {
            throw new NullPointerException("Argument cannot be null (clazz) !");
        }
        if (o == null) {
            logger.warn("Call toXMLStringFromNonRootObject with null object, returns null !");
            return null;
        }
        JAXBContext jc;
        Marshaller m;
        try {
            jc = JAXBContext.newInstance(clazzList);
            m = jc.createMarshaller();
            if (pretty) {
                m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            }
            StringWriter stringWriter = new StringWriter();
            String qRootName = StringUtils.isNotBlank(rootName) ? rootName : clazz.getSimpleName();
            String qNamespaceURI = StringUtils.isNotBlank(namespaceURI) ? namespaceURI : null;

            if (StringUtils.isBlank(qNamespaceURI) && StringUtils.isNotBlank(prefix)) {
                qNamespaceURI = clazz.getPackage().getName();
            }

            String qPrefix = StringUtils.isNotBlank(prefix) ? prefix : null;

            QName qName;
            if (StringUtils.isNotBlank(qNamespaceURI) && StringUtils.isNotBlank(qRootName) && StringUtils.isNotBlank(qPrefix)) {
                qName = new QName(qNamespaceURI, qRootName, qPrefix);
            } else if (StringUtils.isNotBlank(qNamespaceURI) && StringUtils.isNotBlank(qRootName)) {
                qName = new QName(qNamespaceURI, qRootName);
            } else {
                qName = new QName(qRootName);
            }
            m.marshal(new JAXBElement(qName, clazz, o), stringWriter);
            return stringWriter.getBuffer().toString();
        } catch (javax.xml.bind.JAXBException e) {
            throw new JAXBException(e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringWithRootSafe(Object o) throws JAXBException {
        return toXMLStringWithRootSafe(o != null ? o.getClass() : null, o, false);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringWithRootSafe(Object o, boolean pretty) throws JAXBException {
        return toXMLStringWithRootSafe(o != null ? o.getClass() : null, o, pretty);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String toXMLStringWithRootSafe(Class<?> clazz, Object o, boolean pretty) throws JAXBException {
        if (clazz == null) {
            throw new NullPointerException("Argument cannot be null (clazz) !");
        }
        if (o == null) {
            logger.warn("Call toXMLStringWithRootSafe with null object, returns null !");
            return null;
        }
        if (clazz.isAnnotationPresent(XmlRootElement.class)) {
            return toXMLString(o, pretty);
        } else {
            String rootName = null;
            String namespaceURI = null;
            if (clazz.isAnnotationPresent(XmlType.class)) {
                XmlType xmlTypeAnotation = clazz.getAnnotation(XmlType.class);
                rootName = StringUtils.isNotBlank(xmlTypeAnotation.name()) ? xmlTypeAnotation.name() : null;
                namespaceURI = StringUtils.isNotBlank(xmlTypeAnotation.namespace()) ? xmlTypeAnotation.namespace() : null;
            } else {
                rootName = clazz.getSimpleName();
            }
            return toXMLStringFromNonRootObject(clazz, o, pretty, rootName, namespaceURI, null);
        }
    }
}
