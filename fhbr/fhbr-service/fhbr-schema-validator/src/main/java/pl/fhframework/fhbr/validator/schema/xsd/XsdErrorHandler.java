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

package pl.fhframework.fhbr.validator.schema.xsd;

import lombok.Getter;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.api.service.ValidationMessageSeverity;
import pl.fhframework.fhbr.api.service.ValidationResult;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 07/07/2022
 */
public class XsdErrorHandler extends DefaultHandler {

    @Getter
    private ValidationResult validationResult = new ValidationResult();
    private final ValidationMessageFactory validationMessageFactory;

    public XsdErrorHandler(ValidationMessageFactory validationMessageFactory) {
        this.validationMessageFactory = validationMessageFactory;
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        ValidationMessage schemaValidationMessage = prepareValidationMessage(ValidationMessageSeverity.E, e.getLocalizedMessage(), computeCurrentXPath());
        validationResult.addValidationMessage(schemaValidationMessage);
    }

    private String computeCurrentXPath() {
        return null;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        ValidationMessage schemaValidationMessage = prepareValidationMessage(ValidationMessageSeverity.E, e.getLocalizedMessage(), computeCurrentXPath());
        validationResult.addValidationMessage(schemaValidationMessage);
    }

    private ValidationMessage prepareValidationMessage(ValidationMessageSeverity validationMessageSeverity, String message, String pointer) {
        ValidationMessage validationMessage = validationMessageFactory.newInstance();
        validationMessage.setSeverity(validationMessageSeverity);
        validationMessage.setPointer(pointer);
        validationMessage.setMessage(message);
        return validationMessage;
    }
}
