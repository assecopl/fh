package pl.fhframework.dp.commons.jaxb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

public class XXETools {

	private static Logger logger = LoggerFactory.getLogger(XXETools.class);

	/** Feature identifier: XInclude processing */
	private static final String XINCLUDE_FEATURE = "http://apache.org/xml/features/xinclude";
	
	/* Feature identifier: create entiry ref nodes feature. */
	//private static final String CREATE_ENTITY_REF_NODES_FEATURE = Constants.XERCES_FEATURE_PREFIX + Constants.CREATE_ENTITY_REF_NODES_FEATURE;

	/** Feature identifier: disallow-doctype-decl.*/
	private static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";

	/** Feature identifier: load-external-dtd.*/
	private static final String LOAD_EXTERNAL_DTD_FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd";

	/** Feature identifier: load-external-dtd.*/
	private static final String EXTERNAL_GENERAL_ENTITIES_FEATURE = "http://xml.org/sax/features/external-general-entities";

	/** Feature identifier: load-external-dtd.*/
	private static final String EXTERNAL_PARAMETER_ENTITIES_FEATURE = "http://xml.org/sax/features/external-parameter-entities";

	/** Feature identifier: load-external-dtd.*/
	private static final String FEATURE_SECURE_PROCESSING = "http://javax.xml.XMLConstants/feature/secure-processing";

	public static SAXParserFactory getXXESafeSAXParserFactory() {
		SAXParserFactory spf = SAXParserFactory.newInstance();

		//brak odpowiednika dla SaxParserFactory? : DocumentBuilderFactory dbf.setExpandEntityReferences(false);

		try {
			spf.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, true);
		} catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e) {
			logger.error("Error in static SAXParserFactory setFeature " + DISALLOW_DOCTYPE_DECL_FEATURE + ": " + e.getMessage());
		}
		try {
			spf.setFeature(LOAD_EXTERNAL_DTD_FEATURE, false);
		} catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e) {
			logger.error("Error in static SAXParserFactory setFeature " + LOAD_EXTERNAL_DTD_FEATURE + ": " + e.getMessage());
		}
		try {
			spf.setFeature(EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
		} catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e) {
			logger.error("Error in static SAXParserFactory setFeature " + EXTERNAL_GENERAL_ENTITIES_FEATURE + ": " + e.getMessage());
		}
		try {
			spf.setFeature(EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
		} catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e) {
			logger.error("Error in static SAXParserFactory setFeature " + EXTERNAL_PARAMETER_ENTITIES_FEATURE + ": " + e.getMessage());
		}
		try {
			spf.setFeature(FEATURE_SECURE_PROCESSING, true);
		} catch (SAXNotRecognizedException | SAXNotSupportedException | ParserConfigurationException e) {
			logger.error("Error in static SAXParserFactory setFeature " + FEATURE_SECURE_PROCESSING + ": " + e.getMessage());
		}

		spf.setXIncludeAware(false);

		return spf;
	}

//	public static SAXReader getXXESafeSAXReaderFactory() {
//
//		SAXReader reader = new SAXReader();
//
//
//		//SAXParserFactory spf = SAXParserFactory.newInstance();
//
//		//brak odpowiednika dla SaxParserFactory? : DocumentBuilderFactory dbf.setExpandEntityReferences(false);
//
//		try {
//			reader.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, true);
//		} catch (SAXException e) {
//			e.printStackTrace();
//			logger.error("Error in static SAXParserFactory setFeature " + DISALLOW_DOCTYPE_DECL_FEATURE + ": " + e.getMessage());
//		}
//		try {
//			reader.setFeature(LOAD_EXTERNAL_DTD_FEATURE, false);
//		} catch (SAXException e) {
//			logger.error("Error in static SAXParserFactory setFeature " + LOAD_EXTERNAL_DTD_FEATURE + ": " + e.getMessage());
//		}
//		try {
//			reader.setFeature(EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
//		} catch (SAXException e) {
//			logger.error("Error in static SAXParserFactory setFeature " + EXTERNAL_GENERAL_ENTITIES_FEATURE + ": " + e.getMessage());
//		}
//		try {
//			reader.setFeature(EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
//		} catch (SAXException e) {
//			logger.error("Error in static SAXParserFactory setFeature " + EXTERNAL_PARAMETER_ENTITIES_FEATURE + ": " + e.getMessage());
//		}
//		try {
//			reader.setFeature(FEATURE_SECURE_PROCESSING, true);
//		} catch (SAXException e) {
//			logger.error("Error in static SAXParserFactory setFeature " + FEATURE_SECURE_PROCESSING + ": " + e.getMessage());
//		}
//
//		return reader;
//	}


	public static DocumentBuilderFactory getXXESafeDocumentBuilderFactory() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		dbf.setExpandEntityReferences(false);

		try {
			dbf.setFeature(XINCLUDE_FEATURE, false);
		} catch (ParserConfigurationException e) {
			logger.error("Error DocumentBuilderFactory setFeature " + XINCLUDE_FEATURE + ": " + e.getMessage());
		}
		try {
			dbf.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, true);
		} catch (ParserConfigurationException e) {
			logger.error("Error DocumentBuilderFactory setFeature " + DISALLOW_DOCTYPE_DECL_FEATURE + ": " + e.getMessage());
		}
		try {
			dbf.setFeature(LOAD_EXTERNAL_DTD_FEATURE, false);
		} catch (ParserConfigurationException e) {
			logger.error("Error DocumentBuilderFactory setFeature " + LOAD_EXTERNAL_DTD_FEATURE + ": " + e.getMessage());
		}
		try {
			dbf.setFeature(EXTERNAL_GENERAL_ENTITIES_FEATURE, false);
		} catch (ParserConfigurationException e) {
			logger.error("Error DocumentBuilderFactory setFeature " + EXTERNAL_GENERAL_ENTITIES_FEATURE + ": " + e.getMessage());
		}
		try {
			dbf.setFeature(EXTERNAL_PARAMETER_ENTITIES_FEATURE, false);
		} catch (ParserConfigurationException e) {
			logger.error("Error DocumentBuilderFactory setFeature " + EXTERNAL_PARAMETER_ENTITIES_FEATURE + ": " + e.getMessage());
		}
		try {
			dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		} catch (ParserConfigurationException e) {
			logger.error("Error DocumentBuilderFactory setFeature " + ": " + e.getMessage());
		}

		dbf.setXIncludeAware(false);
		
		return dbf;
	}
}
