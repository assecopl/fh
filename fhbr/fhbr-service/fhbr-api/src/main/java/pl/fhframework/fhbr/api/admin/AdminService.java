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
import pl.fhframework.fhbr.api.model.BRuleSetDto;

import java.util.List;

/**
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
     * @param ruleSetDto
     */
    void createRuleSet(BRuleSetDto ruleSetDto) throws AlreadyExistsException;

    void updateRuleSet(BRuleSetDto ruleSetDto);

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
     * unlink rule from rule set
     *
     * @param ruleSetCode - rule set code
     * @param ruleCode    - rule code (unique)
     * @return true when link found and removed otherwise false
     */
    boolean unlinkRule(String ruleSetCode, String ruleCode);


    void registerNewRule(BRuleCfgDto ruleCfgDto, BRuleDefDto ruleDefDto) throws AlreadyExistsException;


}
