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

package pl.fhframework.fhbr.engine.context;

import pl.fhframework.fhbr.api.model.BRuleCfgDto;
import pl.fhframework.fhbr.api.service.*;
import pl.fhframework.fhbr.engine.ValidatorServiceImpl;
import pl.fhframework.fhbr.engine.audit.AuditCheckPoint;
import pl.fhframework.fhbr.engine.audit.AuditPoint;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 31/08/2022
 */
public class DelegateValidationContextImpl implements InternalValidationContext {

    private MainValidationContextImpl mainValidationContext;
    private InternalValidationContext validationContext;

    public DelegateValidationContextImpl(MainValidationContextImpl mainValidationContext) {
        this.mainValidationContext = mainValidationContext;
    }

    public DelegateValidationContextImpl(InternalValidationContext validationContext) {
        this.validationContext = validationContext;
    }

    @Override
    public String getInitialRuleSetCode() {
        return validationContext != null ? validationContext.getInitialRuleSetCode() : mainValidationContext.getInitialRuleSetCode();
    }

    @Override
    public LocalDate getInitialOnDate() {
        return validationContext != null ? validationContext.getInitialOnDate() : mainValidationContext.getInitialOnDate();
    }

    @Override
    public ValidationMessageFactory getMessageFactory() {
        return validationContext != null ? validationContext.getMessageFactory() : mainValidationContext.getMessageFactory();
    }

    public ValidatorServiceImpl getValidatorService() {
        return validationContext != null ? validationContext.getValidatorService() : mainValidationContext.getValidatorService();
    }

    @Override
    public AuditPoint getAuditPoint() {
        return validationContext != null ? validationContext.getAuditPoint() : mainValidationContext.getAuditPoint();
    }

    public void addCheckPoint(String checkPointNAme, String value) {
        this.getAuditPoint().addAuditPoint(new AuditCheckPoint(checkPointNAme, value));
    }

    @Override
    public ValidationMessage createError(String message) {
        ValidationMessage validationMessage = getMessageFactory().newInstance();
        validationMessage.setSeverity(ValidationMessageSeverity.ERROR);
        validationMessage.setMessage(message);
        return validationMessage;
    }

    //-------------------------- below depending on current rule

    @Override
    public List<ValidationMessage> applyRuleSet(String ruleSetCode, ValidateObject validateObject) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> List<ValidationMessage> applyRule(Class<T> clazz, Function<T, List<ValidationMessage>> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void subscribeRule(Class<T> clazz, Function<T, List<ValidationMessage>> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public <T> void subscribeRule(Class<T> clazz, BiFunction<ValidationContext, T, List<ValidationMessage>> function) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<ValidationMessage> runSubscribedRules() {
        throw new UnsupportedOperationException();
    }

    @Override
    public ValidationMessage createMessage(BRuleCfgDto ruleCfg) {
        throw new UnsupportedOperationException();
    }
}
