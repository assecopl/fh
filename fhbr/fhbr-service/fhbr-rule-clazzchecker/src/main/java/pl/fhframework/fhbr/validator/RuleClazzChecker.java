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

package pl.fhframework.fhbr.validator;

import pl.fhframework.fhbr.api.exception.RuleException;
import pl.fhframework.fhbr.api.exception.ValidationException;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.rule.ComplexRule;
import pl.fhframework.fhbr.api.rule.SimpleRule;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.engine.ValidationContextImpl;
import pl.fhframework.fhbr.engine.checker.AbstractRuleChecker;
import pl.fhframework.fhbr.engine.context.InternalValidationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 08/07/2022
 */
public class RuleClazzChecker extends AbstractRuleChecker {

    @Override
    protected List<ValidationMessage> check(Object object, InternalValidationContext context, BRuleDto rule) throws Exception {

        List<ValidationMessage> validationMessages = new ArrayList<>();
        ValidationContextImpl ruleContext = new ValidationContextImpl(context, rule.getConfig());
        try {
            Object ruleInstance = Class.forName(rule.getDefinition().getRuleClassName(), true, this.getClass().getClassLoader()).newInstance();
            List<ValidationMessage> checkResult = null;

            if (ruleInstance instanceof SimpleRule) {
                SimpleRule ruleChecker = (SimpleRule) ruleInstance;
                if (!ruleChecker.isValid(object, context)) {
                    validationMessages.add(ruleContext.getMessageFactory().prepareValidationMessage(rule.getConfig()));
                }
            } else if (ruleInstance instanceof ComplexRule) {
                ComplexRule ruleChecker = (ComplexRule) ruleInstance;
                checkResult = ruleChecker.check(object, ruleContext, rule);
            } else {
                throw new ValidationException("fhbr.validation.unknownRuleType", "ruleType", object.getClass().getName());
            }

            if (checkResult != null) {
                validationMessages.addAll(checkResult);
            }

        } catch (Exception e) {
            if (!(e instanceof ValidationException)) {
                e = new RuleException(rule.getConfig().getRuleCode(), rule.getDefinition().getRuleClassName(), e);
            }
            throw e;
        } finally {
            ruleContext.getAuditPoint().finish();
        }
        return validationMessages;

    }


}
