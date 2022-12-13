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

package pl.fhframework.fhbr.api.admin;

import pl.fhframework.fhbr.api.exception.AlreadyExistsException;
import pl.fhframework.fhbr.api.model.BRuleCfgDto;
import pl.fhframework.fhbr.api.model.BRuleDefDto;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.model.BRuleSetDto;

import java.util.List;

/**
 * Admin service - validation service rules management
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 19/08/2022
 */
public interface AdminService {


    /**
     * List rule sets for given query object
     *
     * @param query - parameters for list
     * @param start
     * @param size
     * @return
     */
    List<BRuleSetDto> findRuleSet(BRuleSetQuery query, int start, int size);

    /**
     * List rules for given query object
     *
     * @param query
     * @param start
     * @param size
     * @return
     */
    List<BRuleCfgDto> findRule(BRuleQuery query, int start, int size);

    /**
     * Create new rule set
     *
     * @param ruleSetDto - rule set configuration
     */
    void registerNewRuleSet(BRuleSetDto ruleSetDto) throws AlreadyExistsException;

    /**
     * Update rule set
     *
     * @param ruleSetDto - rule set configuration
     * @return true if updated
     */
    boolean updateRuleSet(BRuleSetDto ruleSetDto);

    /**
     * Delete ruleSet and all links between that ruleSet and rules
     *
     * @param ruleSetCode - rule set code
     * @return
     */
    boolean deleteRuleSet(String ruleSetCode);

    /**
     * Ling rule to rule set
     *
     * @param ruleSetCode - target rule set
     * @param ruleCode    - rule code
     * @return
     */
    boolean linkRule(String ruleSetCode, String ruleCode);

    /**
     * Un-link rule from rule set
     *
     * @param ruleSetCode - rule set code
     * @param ruleCode    - rule code (unique)
     * @return true when link found and removed otherwise false
     */
    boolean unlinkRule(String ruleSetCode, String ruleCode);


    /**
     * Register new rule only if all requirements are met:
     * - not exists rule with ruleCode
     * - not exist other rule with this same bussinesRuleCode with
     *
     * @param ruleCfgDto - rule configuration
     * @param ruleDefDto - rule definition
     * @return ruleCode (can be overwritten during registration)
     * @throws AlreadyExistsException - if exists according bussinesRuleCode + variant + version
     */
    String registerNewRule(BRuleCfgDto ruleCfgDto, BRuleDefDto ruleDefDto) throws AlreadyExistsException;

    /**
     * Update rule configuration
     *
     * @param ruleCfgDto
     * @return
     */
    boolean updateRuleCfg(BRuleCfgDto ruleCfgDto);

    /**
     * Update rule definition.
     * <p>
     * If after update recompilation of rule is required flag the compilationRequired must be set
     *
     * @param ruleCode   - rule code (unique)
     * @param ruleDefDto - rule definition
     * @return
     */
    boolean updateRuleDef(String ruleCode, BRuleDefDto ruleDefDto);

    /**
     * Compilation rule (if applicable)
     * <p>
     * Urgent: each compilation must generate new class (class name bust be changed)
     *
     * @param ruleCode - rule code
     * @return current version of the rule after compilation
     */
    BRuleDto compileRule(String ruleCode);

}
