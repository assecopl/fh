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

package pl.fhframework.fhbr.dao;

import lombok.Getter;
import pl.fhframework.fhbr.api.dao.BRuleSetDao;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.model.BRuleSetDto;

import java.time.LocalDate;
import java.util.*;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 11/07/2022
 */
public class InMemoryBussinesRuleDao implements BRuleSetDao {

    @Getter
    private Map<String, List<BRuleDto>> ruleSetsRules = new HashMap<>();
    @Getter
    private Map<String, BRuleSetDto> ruleSets = new HashMap<>();

    @Override
    public List<BRuleDto> findRuleSetRules(String ruleSetCode, String phase, boolean active, LocalDate onDate) {
        return ruleSetsRules.containsKey(ruleSetCode) ? new ArrayList<>(ruleSetsRules.get(ruleSetCode)) : new ArrayList<>();
    }

    @Override
    public BRuleSetDto findRuleSet(String ruleSetCode, String phase) {
        return ruleSets.get(ruleSetCode);
    }

    @Override
    public List<BRuleDto> findRule(String ruleCode, String phase, boolean active, LocalDate onDate) {
        Set<BRuleDto> rules = new HashSet<>();

        ruleSetsRules.values().forEach(ruleSetList -> ruleSetList.stream().filter(
                        ruleDto -> ruleCode.equals(ruleDto.getConfig().getRuleCode()))
                .forEach(ruleDto -> rules.add(ruleDto)));
        return new ArrayList<BRuleDto>(rules);
    }

}
