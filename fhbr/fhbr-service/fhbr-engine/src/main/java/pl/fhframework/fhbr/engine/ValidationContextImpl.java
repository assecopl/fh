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

import pl.fhframework.fhbr.api.model.BRuleCfgDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationMessageSeverity;
import pl.fhframework.fhbr.engine.audit.AuditPoint;
import pl.fhframework.fhbr.engine.audit.AuditRuleApply;
import pl.fhframework.fhbr.engine.checker.RuleFunction;
import pl.fhframework.fhbr.engine.context.DelegateValidationContextImpl;
import pl.fhframework.fhbr.engine.context.InternalValidationContext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;

/**
 * Context for single validation request.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 18/08/2022
 */
public class ValidationContextImpl extends DelegateValidationContextImpl {

    private final AuditPoint auditPoint;
    private final BRuleCfgDto ruleCfgDto;
    private final List<RuleFunction<? extends Class<?>>> subscribedFunctionRules = Collections.synchronizedList(new ArrayList<>());

    public ValidationContextImpl(InternalValidationContext parent, BRuleCfgDto ruleCfgDto) {
        super(parent);
        this.ruleCfgDto = ruleCfgDto;
        this.auditPoint = new AuditRuleApply(ruleCfgDto);
        parent.getAuditPoint().addAuditPoint(auditPoint);
    }

    public AuditPoint getAuditPoint() {
        return this.auditPoint;
    }

    public List<ValidationMessage> applyRuleSet(String ruleSetCode, ValidateObject validateObject) {
        List<ValidationMessage> result = getValidatorService().applyRuleSet(this, ruleSetCode, validateObject);
        return result != null ? result : new ArrayList<>();
    }

    @Override
    public <T> List<ValidationMessage> applyRule(Class<T> clazz, BiFunction<ValidationContext, T, List<ValidationMessage>> function) {
        List<ValidationMessage> result = getValidatorService().applyNow(this, new RuleFunction<T>(clazz, function));
        return result != null ? result : new ArrayList<>();
    }

//    @Override
//    public <T> void subscribeRule(Class<T> clazz, Function<T, List<ValidationMessage>> function) {
//        synchronized (this) {
//            subscribedFunctionRules.add(new RuleFunction(clazz, function));
//        }
//    }

    @Override
    public <T> void subscribeRule(Class<T> clazz, BiFunction<ValidationContext, T, List<ValidationMessage>> function) {
        synchronized (this) {
            subscribedFunctionRules.add(new RuleFunction(clazz, function));
        }
    }

    @Override
    public List<ValidationMessage> runSubscribedRules() {
        List<RuleFunction<? extends Class<?>>> functionRulesList;
        synchronized (this) {
            // copy
            functionRulesList = new ArrayList<>(this.subscribedFunctionRules);
            // clear
            this.subscribedFunctionRules.clear();
        }
        // applyNow for copy
        List<ValidationMessage> result = getValidatorService().applyNow(this, functionRulesList);

        return result;
    }


    public ValidationMessage createMessage() {
        return getMessageFactory().prepareValidationMessage(this.ruleCfgDto);
    }

    @Override
    public ValidationMessage createError(String message) {
        ValidationMessage msg = this.createMessage();
        msg.setSeverity(ValidationMessageSeverity.ERROR);
        msg.setMessage(message);
        return msg;
    }
}
