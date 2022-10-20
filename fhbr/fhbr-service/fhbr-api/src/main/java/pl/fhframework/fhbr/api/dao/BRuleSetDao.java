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

package pl.fhframework.fhbr.api.dao;

import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.model.BRuleSetDto;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO for BRuleSet
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
public interface BRuleSetDao {

    /**
     * Find ruleSet.
     *
     * @param ruleSetCode - rule set code
     * @return - ruleSet data or null if not exists
     */
    BRuleSetDto findRuleSet(String ruleSetCode);

    /**
     * Find rules linked to rule set.
     *
     * @param ruleSetCode - rule set code
     * @param active      - active rule (turn on)
     * @param onDate      - on date between validFrom and validTo
     * @return rules or empty list if not exists
     */
    List<BRuleDto> findRuleSetRules(String ruleSetCode, boolean active, LocalDate onDate);

    /**
     * Find active/unique rule on date
     *
     * @param businessRuleCode - business rule code
     * @param onDate           - on date between validFrom and validTo
     * @return - rule data or null
     */
    BRuleDto findActiveRule(String businessRuleCode, LocalDate onDate);

    /**
     * Find active rules on date.
     * <p>
     * Urgent: for the businessRuleCode only one rule can be active and returned
     *
     * @param businessRuleCode - business rule code
     * @param onDate           -  rule with period defined by validFrom - validTo contains the onDate
     * @return list of uniques rules
     */
    List<BRuleDto> findActiveRules(List<String> businessRuleCode, LocalDate onDate);

}
