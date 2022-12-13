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
package pl.fhframework.fhbr.api.service.impl;

import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageSeverity;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public class ValidationMessageImpl implements ValidationMessage {

    private String message; //message or message key
    private ValidationMessageSeverity severity; // default warning
    private String pointer;
    private String ruleCode;

    public ValidationMessageImpl(ValidationMessageSeverity severity, String message, String pointer) {
        this.message = message;
        this.pointer = pointer;
        this.setSeverity(severity);
    }

    public ValidationMessageImpl() {
        this.setSeverity(ValidationMessageSeverity.WARNING);
    }

    @Override
    public String getMessage() {
        return message;
    }


    @Override
    public void setMessage(String message) {
        this.message = message;
    }


    @Override
    public ValidationMessageSeverity getSeverity() {
        return severity;
    }


    @Override
    public void setSeverity(ValidationMessageSeverity severity) {
        if (severity == null) {
            throw new IllegalArgumentException("The message severity can't bu null");
        }
        this.severity = severity;
    }


    @Override
    public String getPointer() {
        return pointer;
    }


    @Override
    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    @Override
    public String getRuleCode() {
        return ruleCode;
    }

    @Override
    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder().append("{");
        if (severity != null)
            sb.append("\"severity\": \"").append(severity).append("\"");
        if (ruleCode != null)
            sb.append(sb.length() > 1 ? ", " : "").append("\"ruleCode\": \"").append(ruleCode).append("\"");
        ;
        if (message != null)
            sb.append(sb.length() > 1 ? ", " : "").append("\"message\": \"").append(message).append("\"");
        ;
        if (pointer != null)
            sb.append(sb.length() > 1 ? ", " : "").append("\"pointer\": \"").append(pointer).append("\"");
        ;
        sb.append("}");

        return sb.toString();
    }
}
