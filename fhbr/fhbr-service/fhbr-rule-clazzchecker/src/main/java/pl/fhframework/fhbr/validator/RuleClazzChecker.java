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

import org.slf4j.LoggerFactory;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.rule.ComplexRule;
import pl.fhframework.fhbr.api.rule.SimpleRule;
import pl.fhframework.fhbr.api.service.ValidateContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.engine.checker.AbstractRuleChecker;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 08/07/2022
 */
public class RuleClazzChecker extends AbstractRuleChecker {

    @Override
    protected List<ValidationMessage> check(Object object, ValidateContext context, BRuleDto rule) throws Exception {

        List<ValidationMessage> validationMessages = new ArrayList<>();
        try {
            Object ruleInstance = Class.forName(rule.getRuleClass(), true, this.getClass().getClassLoader()).newInstance();

            if (ruleInstance instanceof SimpleRule) {
                SimpleRule ruleChecker = (SimpleRule) ruleInstance;
                if (!ruleChecker.isValid(object, context)) {
                    validationMessages.add(context.getMessageFactory().prepareValidationMessage(rule));
                }
            } else {
                ComplexRule ruleChecker = (ComplexRule) ruleInstance;
                List<ValidationMessage> checkResult = ruleChecker.check(object, context, rule);
                if (checkResult != null) {
                    validationMessages.addAll(checkResult);
                }
            }
        } catch (Exception e) {
            LoggerFactory.getLogger(RuleClazzChecker.class).error("Rule '{}' - {} [{}]: {} - error: {}",
                    rule.getName(),
                    rule.getBusinessKey(),
                    rule.getId(), rule.getRuleExpression(), e);
            throw e;
        }
        return validationMessages;

    }


}