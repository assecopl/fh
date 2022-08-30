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

package pl.fhframework.fhbr.api.service;

import pl.fhframework.fhbr.api.model.BRuleCfgDto;

import java.time.LocalDate;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Context for single validation request.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 13/07/2022
 */
public interface ValidationContext {

    /**
     * Initial rule set
     */
    String getInitialRuleSetCode();

    /**
     * Initial onDate passed to service call - validation date
     * (used for acquire rules from the repository)
     */
    LocalDate getInitialOnDate();

    /**
     * Getter for MessageFactory.
     * <p>
     * Use for create ValidationMessage inside rule body.
     * e.g.
     * ValidationMessage m = context.getMessageFactory().newInstance();
     * ... // set severity / message / pointer etc.
     * validationResult.addValidationMessage(m);
     *
     * @return
     */
    ValidationMessageFactory getMessageFactory();

    /**
     * Apply additional rule set during current validation process
     * - on this same or new (other) object
     * <p>
     * Usage during rule typu 'flow'' (ComplexRule), where additional decision must be done
     * before run other rule set.
     * <p>
     * Result list of ValidationMessage aren't rewrite to validationResult.
     * It can be done manually:
     * e.g. validationMessages.stream().forEach(vm -> validationResult.addValidationMessage(vm));
     *
     * @param ruleSetCode    - any ruleSet
     * @param validateObject - target object wrapper
     * @return list of ValidationMessages
     */
    List<ValidationMessage> applyRuleSet(String ruleSetCode, ValidateObject validateObject);

    <T> List<ValidationMessage> applyRule(Class<T> clazz, Function<T, List<ValidationMessage>> function);

    <T> void subscribeRule(Class<T> clazz, Function<T, List<ValidationMessage>> function);

    <T> void subscribeRule(Class<T> clazz, BiFunction<ValidationContext, T, List<ValidationMessage>> function);

    List<ValidationMessage> runSubscribedRules();

    ValidationMessage createError(String ruleCode, String message);

    ValidationMessage createMessage(BRuleCfgDto ruleCfg);

}
