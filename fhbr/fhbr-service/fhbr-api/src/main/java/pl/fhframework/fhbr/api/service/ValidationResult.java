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
package pl.fhframework.fhbr.api.service;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public class ValidationResult {

    private boolean valid = true;

    private List<ValidationMessage> validationMessages = Collections.synchronizedList(new LinkedList<ValidationMessage>());

    public ValidationResult() {
    }

    public ValidationResult(boolean valid, List<ValidationMessage> validationMessages) {
        this.valid = valid;
        if (validationMessages != null) {
            this.validationMessages.addAll(validationMessages);
        }
    }

    /**
     * Result of validation
     *
     * @return true - ok
     * false - not ok (in default each message with severity above WARNING makes result invalid)
     */
    public boolean getValid() {
        return valid;
    }

    protected void setValid(boolean status) {
        this.valid = status;
    }

    public List<ValidationMessage> getValidationResultMessages() {
        return Collections.unmodifiableList(validationMessages);
    }

    public void addValidationMessage(ValidationMessage validationMessage) {
        if (validationMessage != null) {
            if (ValidationMessageSeverity.isErrorOrAbove(validationMessage.getSeverity())) {
                valid = false;
            }
            validationMessages.add(validationMessage);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{ \"valid\": ").append(valid);
        sb
                .append(", \"validationMessages\": ")
                .append(Arrays.toString(getValidationResultMessages().toArray()));
        return sb.append(" }").toString();
    }
}
