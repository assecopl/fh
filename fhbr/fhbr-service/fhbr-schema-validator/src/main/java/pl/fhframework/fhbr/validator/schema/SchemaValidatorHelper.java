/*
 * Copyright 2019 Asseco Poland S.A.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this work except in compliance with the License.
 * You may obtain a copy of the License in the LICENSE file,
 * or at: http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package pl.fhframework.fhbr.validator.schema;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.xerces.impl.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.SAXException;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.api.service.ValidationMessageSeverity;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.validator.schema.exception.UnknownNamespace;
import pl.fhframework.fhbr.validator.schema.xsd.XsdErrorHandler;
import pl.fhframework.fhbr.validator.schema.xsd.XsdResolverFactory;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by dariuszs on 22.09.2016.
 */
public class SchemaValidatorHelper {

    private static final String SAX_PARSER_FACTORY = "org.apache.xerces.jaxp.validation.XMLSchemaFactory";
    private static final String SAX_PARSER_FACTORY_CLASS = "org.apache.xerces.jaxp.SAXParserFactoryImpl";

    private final Logger logger = LoggerFactory.getLogger(SchemaValidatorHelper.class);

    private final XsdResolverFactory xsdResolverFactory;
    private final ValidationMessageFactory validationMessageFactory;

    public SchemaValidatorHelper(XsdResolverFactory xsdResolverFactory, ValidationMessageFactory validationMessageFactory) {
        this.xsdResolverFactory = xsdResolverFactory;
        this.validationMessageFactory = validationMessageFactory;
    }

    public ValidationResult validate(String namespace, byte[] content) {
        return validate(namespace, content, prepareXsdResolver());
    }

    public ValidationResult validate(String namespace, byte[] content, LSResourceResolver lsResourceResolver) {
        ValidationResult result;
        XsdErrorHandler errorHandler = new XsdErrorHandler(validationMessageFactory);
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        factory.setResourceResolver(lsResourceResolver);
        factory.setErrorHandler(errorHandler);
        Schema schema = null;
        try {
            LSInput lsInput = lsResourceResolver.resolveResource(null, namespace, null, null, null);
            if (lsInput == null) {
                return fromException("Unknown namespace", new UnknownNamespace(namespace));
            }
            StreamSource ss = new StreamSource(new BOMInputStream(lsInput.getByteStream()));
            schema = factory.newSchema(ss);

            if (!errorHandler.getValidationResult().getValidationResultMessages().isEmpty()) {
                result = errorHandler.getValidationResult();
            } else {
                result = validate(namespace, content, schema);
            }
        } catch (SAXException e) {
            result = fromException("Invalid namespace '" + namespace + "'.", e);
        }

        return result;
    }

    private ValidationResult fromException(String message, Exception e) {
        logger.warn(message, e);
        ValidationResult result = new ValidationResult();
        ValidationMessage msg = validationMessageFactory.newInstance();
        msg.setSeverity(ValidationMessageSeverity.E);
        msg.setMessage(message);
        result.addValidationMessage(msg);
        return result;
    }

    public ValidationResult validate(byte[] content, Schema schema) throws Exception {
        SAXParserFactory parserFactory = SAXParserFactory.newInstance(SAX_PARSER_FACTORY_CLASS, null);
        parserFactory.setNamespaceAware(true);
        parserFactory.setSchema(schema);

        SAXParser saxParser = parserFactory.newSAXParser();
        XsdErrorHandler errorHandler = new XsdErrorHandler(validationMessageFactory);
        saxParser.parse(new BOMInputStream(new ByteArrayInputStream(content)), errorHandler);
        return errorHandler.getValidationResult();
    }

    private ValidationResult validate(String namespace, byte[] content, Schema schema) {

        SAXParserFactory parserFactory = SAXParserFactory.newInstance(SAX_PARSER_FACTORY_CLASS, null);
        parserFactory.setNamespaceAware(true);
        parserFactory.setSchema(schema);

        SAXParser saxParser;
        try {
            saxParser = parserFactory.newSAXParser();
        } catch (ParserConfigurationException e) {
            return fromException("Invalid namespace config '" + namespace + "'.", e);
        } catch (SAXException e) {
            return fromException("Invalid namespace '" + namespace + "'.", e);
        }

        XsdErrorHandler errorHandler = new XsdErrorHandler(validationMessageFactory);
        try {
            saxParser.parse(new BOMInputStream(new ByteArrayInputStream(content)), errorHandler);
        } catch (SAXException | IOException e) {
            return fromException("Parse error for namespace'" + namespace + "'", e);
        }

        return errorHandler.getValidationResult();
    }

    public ValidationResult validateXSD(byte[] content) {

        ValidationResult result;
        String namespace = "http://www.w3.org/2001/XMLSchema";

        InputStream xmlScheme_xsd = this.getClass().getResourceAsStream("/schema/XMLSchema.xsd");

        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI, SAX_PARSER_FACTORY, null);
        factory.setResourceResolver(prepareXsdResolver());
        try {
            Schema schema = factory.newSchema(new StreamSource[]{new StreamSource(xmlScheme_xsd)});
            result = validate(namespace, content, schema);
        } catch (SAXException e) {

            result = fromException("Invalid schema '" + namespace + "'.", e);
        }
        return result;

    }

    public Schema buildSchema(StreamSource[] xsdStreamSource) throws Exception {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI, SAX_PARSER_FACTORY, null);
        factory.setFeature(Constants.XERCES_FEATURE_PREFIX + Constants.NAMESPACE_GROWTH_FEATURE, true);
        factory.setResourceResolver(prepareXsdResolver());
        return factory.newSchema(xsdStreamSource);
    }

    private LSResourceResolver prepareXsdResolver() {
        return xsdResolverFactory.newInstance();
    }

}
