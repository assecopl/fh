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
package pl.fhframework.fhbr.api.model;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public class ValidationMessage {

    private String message; //message or message key
    private ValidationMessageSeverity validationMessageSeverity;
    private String pointer;
    private String ruleCode;

    public ValidationMessage(ValidationMessageSeverity validationMessageSeverity, String message, String pointer) {
        this.validationMessageSeverity = validationMessageSeverity;
        this.message = message;
        this.pointer = pointer;
    }


    public ValidationMessage() {
    }

   
    public String getMessage() {
        return message;
    }

   
    public void setMessage(String message) {
        this.message = message;
    }

   
    public ValidationMessageSeverity getSeverity() {
        return validationMessageSeverity;
    }

   
    public void setSeverity(ValidationMessageSeverity severity) {
        this.validationMessageSeverity = severity;
    }

   
    public String getPointer() {
        return pointer;
    }

   
    public void setPointer(String pointer) {
        this.pointer = pointer;
    }

    public String getRuleCode() {
        return ruleCode;
    }

    public void setRuleCode(String ruleCode) {
        this.ruleCode = ruleCode;
    }

   
    public String toString() {
        return " " + (validationMessageSeverity!=null ? validationMessageSeverity : "") + "\n\r " + message +
                (ruleCode != null ? " (" + ruleCode + ")" : "" ) +
                (pointer != null ? " [" + pointer + "]" : "" ) + ". \n\r";
    }
}
