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

package fhbr.validator.schema.xsd;

import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.xerces.dom.DOMXSImplementationSourceImpl;
import org.fhbr.api.dao.XsdRepositoryDao;
import org.slf4j.LoggerFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;

import java.io.ByteArrayInputStream;
import java.time.LocalDate;

/**
 * Created by dariuszs on 13.07.2013.
 */
public class XsdResolver implements LSResourceResolver {

    private final LocalDate onDate;
    protected XsdRepositoryDao xsdRepositoryDao;
    protected DOMImplementationLS domImplementationLS;

    protected XsdResolver() {
        this(null, LocalDate.now());
    }

    public XsdResolver(XsdRepositoryDao xsdRepositoryDao, LocalDate onDate) {
        this.xsdRepositoryDao = xsdRepositoryDao;
        this.onDate = onDate;
        DOMImplementationRegistry registry = null;
        try {
            registry = DOMImplementationRegistry.newInstance();
        } catch (Exception e) {
            try {
                System.setProperty(DOMImplementationRegistry.PROPERTY, DOMXSImplementationSourceImpl.class.getName());
                registry = DOMImplementationRegistry.newInstance();
            } catch (ClassNotFoundException |InstantiationException |IllegalAccessException e2) {
                LoggerFactory.getLogger(XsdResolver.class).error("Error create DOMImplementationLS 2", e2);
                throw new RuntimeException("Error create DOMImplementationLS", e);
            }
        }
        domImplementationLS = (DOMImplementationLS) registry.getDOMImplementation("LS");
    }

    @Override
    public LSInput resolveResource(String type, String namespaceURI, String publicId, String systemId, String baseURI) {
        LSInput lsInput = null;

        if (xsdRepositoryDao != null) {
            if (StringUtils.isNotBlank(namespaceURI)) {

                byte[] schemeXsd = xsdRepositoryDao.getByNamespace(namespaceURI, onDate);
                if (schemeXsd != null) {
                    lsInput = domImplementationLS.createLSInput();
                    BOMInputStream inputStream = new BOMInputStream(new ByteArrayInputStream(schemeXsd));
                    lsInput.setByteStream(inputStream);
                    return lsInput;
                }
            } else if (StringUtils.isNotBlank(publicId)) {

                byte[] schemeXsd = xsdRepositoryDao.getByPublicId(publicId, onDate);
                if (schemeXsd != null) {
                    lsInput = domImplementationLS.createLSInput();
                    BOMInputStream inputStream = new BOMInputStream(new ByteArrayInputStream(schemeXsd));
                    lsInput.setByteStream(inputStream);
                    lsInput.setPublicId(publicId);
                    return lsInput;
                }
            }
        }

        //standardowe schematy
        if ("http://www.w3.org/2001/XMLSchema".equals(namespaceURI)) {
            lsInput = domImplementationLS.createLSInput();
            try {
                BOMInputStream inputStream = new BOMInputStream((this.getClass().getResourceAsStream("/schema/XMLSchema.xsd")));
                lsInput.setByteStream(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return lsInput;
        } else if (systemId != null && systemId.endsWith("XMLSchema.dtd")) {
            lsInput = domImplementationLS.createLSInput();
            try {
                BOMInputStream inputStream = new BOMInputStream((this.getClass().getResourceAsStream("/schema/XMLSchema.dtd")));
                lsInput.setByteStream(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return lsInput;
        }
        if (systemId != null && systemId.endsWith("datatypes.dtd")) {
            lsInput = domImplementationLS.createLSInput();
            try {
                BOMInputStream inputStream = new BOMInputStream((this.getClass().getResourceAsStream("/schema/datatypes.dtd")));
                lsInput.setByteStream(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return lsInput;
        }
        if (systemId != null && systemId.endsWith("xml.xsd") || "http://www.w3.org/XML/1998/namespace".equals(namespaceURI)) {
            lsInput = domImplementationLS.createLSInput();
            try {
                BOMInputStream inputStream = new BOMInputStream((this.getClass().getResourceAsStream("/schema/xml.xsd")));
                lsInput.setByteStream(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return lsInput;
        }
        if ("http://uri.etsi.org/01903/v1.3.2#".equals(namespaceURI)) {
            lsInput = domImplementationLS.createLSInput();
            try {
                BOMInputStream inputStream = new BOMInputStream((this.getClass().getResourceAsStream("/schema/XAdES.xsd")));
                lsInput.setByteStream(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return lsInput;
        }
        if ("http://www.w3.org/2000/09/xmldsig#".equals(namespaceURI)) {
            lsInput = domImplementationLS.createLSInput();
            try {
                BOMInputStream inputStream = new BOMInputStream((this.getClass().getResourceAsStream("/schema/xmldsig-core-schema.xsd")));
                lsInput.setByteStream(inputStream);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
            return lsInput;
        }


        return lsInput;
    }

}
