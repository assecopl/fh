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

package pl.fhframework.fhbr.engine;

import lombok.Getter;
import pl.fhframework.fhbr.api.service.ValidateContext;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.engine.audit.AuditContextData;

import java.util.List;

/**
 * Context for single validation request.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 18/08/2022
 */
public class ValidateContextImpl implements ValidateContext {

    @Getter
    private final String initialRuleSetCode;
    @Getter
    private final String initialPhase;

    @Getter
    private ValidationMessageFactory messageFactory;
    private final ValidatorServiceImpl validatorService;
    private final AuditContextData auditContextData;

    public ValidateContextImpl(ValidationMessageFactory messageFactory, ValidatorServiceImpl validatorService, String initialRuleSetCode, String initialPhase) {
        this.messageFactory = messageFactory;
        this.validatorService = validatorService;
        this.initialRuleSetCode = initialRuleSetCode;
        this.initialPhase = initialPhase;
        this.auditContextData = new AuditContextData();
    }


    public List<ValidationMessage> applyRuleSet(String ruleSetCode, String phase, ValidateObject validateObject) {
        return validatorService.applyRuleSet(this, ruleSetCode, phase, validateObject);
    }

    public List<ValidationMessage> applyRuleSet(String ruleSetCode, ValidateObject validateObject) {
        return applyRuleSet(ruleSetCode, null, validateObject);
    }

    public List<ValidationMessage> subscribeRule(String ruleCode, ValidateObject validateObject) {
        return validatorService.applyRule(this, ruleCode, null, validateObject);

    }


    void addCheckPoint(String checkPointNAme, String value) {
        auditContextData.addCheckPoint(checkPointNAme, value);
    }


}
