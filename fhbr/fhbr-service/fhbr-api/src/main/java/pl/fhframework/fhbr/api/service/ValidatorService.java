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

import java.time.LocalDate;

/**
 * The validator service internal (direct) interface.
 * <p>
 * Verify correctness of the passed object according
 * - schema (structure)
 * - list (reference data)
 * - rule
 * compiled in the named module.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public interface ValidatorService {

    /**
     * Valiadtion
     *
     * @param ruleSetCode - rule set code (named set of services and rules)
     *                    Can only be null when validate object contains xml message  then
     *                    data parser select module automatically based on namespace and root element name
     * @param target      - object for check
     * @return result of validation
     */
    ValidationResult validate(String ruleSetCode, ValidateObject target);

    /**
     * Check that rule with business code can by run
     *
     * @param businessRuleCode
     * @param onDay            -
     * @return
     */
    boolean canIRun(String businessRuleCode, LocalDate onDay);
}
