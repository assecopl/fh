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
import pl.fhframework.fhbr.api.model.BRuleCfgDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.engine.audit.AuditContextData;
import pl.fhframework.fhbr.engine.checker.FunctionRule;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

/**
 * Context for single validation request.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 18/08/2022
 */
public class ValidationContextImpl implements ValidationContext {

    @Getter
    private final String initialRuleSetCode;
    @Getter
    private final LocalDate initialOnDate;

    @Getter
    private ValidationMessageFactory messageFactory;
    private final ValidatorServiceImpl validatorService;
    private final AuditContextData auditContextData;

    @Getter
    private List<FunctionRule<? extends Class<?>>> subscribedFunctionRules = Collections.synchronizedList(new ArrayList<>());

//    @Getter
//    private List<Function<T, List<ValidationMessage>>> subribedValidators ; //= Collections.synchronizedList(new ArrayList<>());

    public ValidationContextImpl(ValidationMessageFactory messageFactory, ValidatorServiceImpl validatorService, String initialRuleSetCode, LocalDate onDate) {
        this.messageFactory = messageFactory;
        this.validatorService = validatorService;
        this.initialRuleSetCode = initialRuleSetCode;
        this.initialOnDate = onDate != null ? onDate : LocalDate.now();
        this.auditContextData = new AuditContextData();
    }


    public List<ValidationMessage> applyRuleSet(String ruleSetCode, ValidateObject validateObject) {
        List<ValidationMessage> result = validatorService.applyRuleSet(this, ruleSetCode, validateObject);
        return result != null ? result : new ArrayList<>();
    }

    @Override
    public <T> List<ValidationMessage> applyRule(Class<T> clazz, Function<T, List<ValidationMessage>> function) {
        List<ValidationMessage> result = validatorService.applyNow(this, clazz, function);
        return result != null ? result : new ArrayList<>();

    }

    @Override
    public <T> void subscribeRule(Class<T> clazz, Function<T, List<ValidationMessage>> function) {
        synchronized (this) {
            subscribedFunctionRules.add(new FunctionRule(clazz, function));
        }
    }

    @Override
    public List<ValidationMessage> runSubscribedRules() {
        List<FunctionRule<? extends Class<?>>> functionRulesList;
        synchronized (this) {
            // copy
            functionRulesList = new ArrayList<>(this.subscribedFunctionRules);
            // clear
            this.subscribedFunctionRules.clear();
        }
        // applyNow for copy
        List<ValidationMessage> result = validatorService.applyNow(this, functionRulesList);

        return result;
    }


    public ValidationMessage createError(String ruleCode, String message) {
        ValidationMessage msg = getMessageFactory().newInstance();
        msg.setRuleCode(ruleCode);
        msg.setMessage(message);
        return msg;
    }

    public ValidationMessage createMessage(BRuleCfgDto ruleCfg) {
        return getMessageFactory().prepareValidationMessage(ruleCfg);
    }

    void addCheckPoint(String checkPointNAme, String value) {
        auditContextData.addCheckPoint(checkPointNAme, value);
    }


}
