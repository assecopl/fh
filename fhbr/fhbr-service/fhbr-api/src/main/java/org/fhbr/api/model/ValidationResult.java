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
package org.fhbr.api.model;

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

    private ValidationResultStatus validationResultStatus = ValidationResultStatus.OK;

    private List<ValidationMessage> validationMessages = new LinkedList<ValidationMessage>();

    public ValidationResult() {
    }

    public ValidationResult(ValidationResultStatus validationResultStatus, List<ValidationMessage> validationMessages) {
        this.validationResultStatus = validationResultStatus;
        if (validationMessages != null) {
            this.validationMessages.addAll(validationMessages);
        }
    }


    public ValidationResultStatus getValidationResultStatus() {
        return validationResultStatus;
    }


    public void setValidationResultStatus(ValidationResultStatus status) {
        this.validationResultStatus = status;
    }


    public List<ValidationMessage> getValidationResultMessages() {
        return Collections.unmodifiableList(validationMessages);
    }

    public void addValidationMessage(ValidationMessage validationMessage) {
        if (validationMessage!=null) {
            if (ValidationMessageSeverity.E.equals(validationMessage.getSeverity())) {
                validationResultStatus = ValidationResultStatus.NOK;
            }
            validationMessages.add(validationMessage);
        }
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(validationResultStatus)
                .append(" ")
                .append(Arrays.toString(getValidationResultMessages().toArray()));
        return sb.toString();
    }
}
