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

    private LinkedList<BRuleDto> rules = new LinkedList<>();
    private Map<String, List<BRuleDto>> ruleSetsRules = new HashMap<>();
    private Map<String, BRuleSetDto> ruleSets = new HashMap<>();

    public BRuleSetDto createRuleSet(String ruleSetName) {
        BRuleSetDto ruleSetDto = new BRuleSetDto();
        ruleSets.put(ruleSetName, ruleSetDto);
        ruleSetsRules.put(ruleSetName, new ArrayList<>());
        return ruleSetDto;
    }

    public void addRule(BRuleDto ruleDto) {
        rules.add(ruleDto);
    }

    public void linkRule(String ruleSetName, BRuleDto ruleDto) {
        if (!rules.contains(ruleDto)) {
            addRule(ruleDto);
        }
        if (!ruleSets.containsKey(ruleSetName)) {
            createRuleSet(ruleSetName);
        }
        ruleSetsRules.get(ruleSetName).add(ruleDto);
    }


    @Override
    public List<BRuleDto> findRuleSetRules(String ruleSetCode, boolean active, LocalDate onDate) {
        return ruleSetsRules.containsKey(ruleSetCode) ? new ArrayList<>(ruleSetsRules.get(ruleSetCode)) : new ArrayList<>();
    }

    @Override
    public BRuleSetDto findRuleSet(String ruleSetCode) {
        return ruleSets.get(ruleSetCode);
    }

    @Override
    public BRuleDto findActiveRule(String businessRuleCode, LocalDate onDate) {
        HashSet<BRuleDto> result = new HashSet<>();

        rules.stream().filter(
                        ruleDto -> businessRuleCode.equals(ruleDto.getConfig().getBusinessRuleCode()) &&
                                ruleDto.getConfig().isActive()
                                && onDate != null && (
                                (ruleDto.getConfig().getValidFrom() == null || (ruleDto.getConfig().getValidFrom() != null && (!ruleDto.getConfig().getValidFrom().isAfter(onDate)))) &&
                                        (ruleDto.getConfig().getValidTo() == null || (ruleDto.getConfig().getValidTo() != null && (!onDate.isAfter(ruleDto.getConfig().getValidTo()))))
                        ))
                .forEach(ruleDto -> result.add(ruleDto));

        //TODO: throw error if more than one

        return result.isEmpty() ? null : result.iterator().next();
    }

    @Override
    public List<BRuleDto> findActiveRules(List<String> businessRuleCodes, LocalDate onDate) {
        List<BRuleDto> result = new ArrayList<>();
        businessRuleCodes.stream().forEach(businessRuleCode ->
        {
            BRuleDto activeRule = findActiveRule(businessRuleCode, onDate);
            if (activeRule != null) {
                result.add(activeRule);
            }
        });
        return result;
    }

}
