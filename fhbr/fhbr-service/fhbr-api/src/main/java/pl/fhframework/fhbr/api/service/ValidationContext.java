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

import java.util.List;

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
     * Initial phase passed to service call
     */
    String getInitialPhase();

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
     * @param phase          - any phase
     * @param validateObject - target object wrapper
     * @return list of ValidationMessages
     */
    List<ValidationMessage> applyRuleSet(String ruleSetCode, String phase, ValidateObject validateObject);

    List<ValidationMessage> applyRuleSet(String ruleSetCode, ValidateObject validateObject);

    List<ValidationMessage> subscribeRule(String ruleSetCode, ValidateObject validateObject);

}
