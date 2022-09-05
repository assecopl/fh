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

package pl.fhframework.fhbr.engine.factory;

import org.slf4j.LoggerFactory;
import pl.fhframework.fhbr.api.exception.RuleCreationException;
import pl.fhframework.fhbr.api.exception.RuleException;
import pl.fhframework.fhbr.api.model.BRuleDto;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 30/08/2022
 */
public class RuleInstanceFactoryImpl {

    public Object getRuleInstance(BRuleDto rule) throws RuleException {
        String ruleClassName = null;
        String ruleCode = null;
        try {
            ruleCode = rule.getConfig().getRuleCode();
            ruleClassName = rule.getDefinition().getRuleClassName();
            Object ruleInstance = Class.forName(ruleClassName, true, this.getClass().getClassLoader()).newInstance();

            return ruleInstance;
        } catch (Exception e) {
            LoggerFactory.getLogger(RuleInstanceFactoryImpl.class).error("Rule creation error '{}', class name {} - error: {}",
                    ruleCode, ruleClassName, e);
            throw new RuleCreationException(ruleCode, e);
        }
    }
}
