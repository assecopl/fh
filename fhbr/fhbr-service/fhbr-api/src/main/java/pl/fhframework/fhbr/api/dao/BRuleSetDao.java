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

    List<BRuleDto> findRuleSetRules(String ruleSetCode, String phase, boolean active, LocalDate onDate);

    BRuleSetDto findRuleSet(String ruleSetCode, String phase);

    List<BRuleDto> findRule(String ruleCode, String phase, boolean active, LocalDate onDate);

}
