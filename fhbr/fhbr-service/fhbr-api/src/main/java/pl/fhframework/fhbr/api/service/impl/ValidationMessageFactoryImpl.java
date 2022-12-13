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

import pl.fhframework.fhbr.api.model.BRuleCfgDto;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.api.service.ValidationMessageSeverity;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 11/07/2022
 */
public class ValidationMessageFactoryImpl implements ValidationMessageFactory<ValidationMessage> {


    @Override
    public ValidationMessage newInstance() {
        return new ValidationMessageImpl();
    }

    @Override
    public ValidationMessage prepareValidationMessage(BRuleCfgDto rule) {
        ValidationMessage message = new ValidationMessageImpl();
        message.setSeverity(ValidationMessageSeverity.fromString(rule.getSeverity()));
        message.setPointer(rule.getPointer());
        message.setMessage(rule.getMessageKey() != null ? rule.getMessageKey() : rule.getMessage());
        message.setRuleCode(rule.getBusinessRuleCode());
        return message;
    }
}
