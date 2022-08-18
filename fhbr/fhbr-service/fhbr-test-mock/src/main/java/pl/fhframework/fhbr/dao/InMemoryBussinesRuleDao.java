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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 11/07/2022
 */
public class InMemoryBussinesRuleDao implements BRuleSetDao {

    @Getter
    private Map<String, List<BRuleDto>> storage = new HashMap<>();
    @Getter
    private Map<String, BRuleSetDto> modules = new HashMap<>();

    @Override
    public List<BRuleDto> findRules(String ruleSetCode, String phase, boolean active, LocalDate onDate) {
        return storage.containsKey(ruleSetCode) ? new ArrayList<>(storage.get(ruleSetCode)) : new ArrayList<>();
    }

    @Override
    public BRuleSetDto findRuleSet(String ruleSetCode, String phase) {
        return modules.get(ruleSetCode);
    }

}
