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

package pl.fhframework.fhbr.engine.checker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.fhbr.api.checker.CheckerTypeService;
import pl.fhframework.fhbr.api.config.ValidatorFeature;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.service.ValidateContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.api.service.ValidationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 08/07/2022
 */
public abstract class AbstractRuleChecker implements CheckerTypeService {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public ValidationResult validate(Object object, ValidateContext context, List<BRuleDto> rules) {

        ValidationResult validationResult = new ValidationResult();
        boolean trace = Boolean.parseBoolean(System.getProperty(ValidatorFeature.RULE_TRACE, "false"));

        int warn_duration = 250;
        try {
            warn_duration = Integer.parseInt(System.getProperty(ValidatorFeature.RULE_WARN_DURATION, "100"));
        } catch (NumberFormatException ignore) {
        }

        for (BRuleDto rule : rules) {
            long time = System.nanoTime();
            try {
                check(object, context, rule).stream().forEach(m -> {
                    validationResult.addValidationMessage(m);
                });
            } catch (Exception e) {
                throw new RuntimeException("Execution error '" + rule.getName() + "' : " + (rule.getBusinessKey() != null ? rule.getBusinessKey() : ""), e);
            } finally {
                BigDecimal duration = new BigDecimal((System.nanoTime() - time) / (1000000.0)).setScale(1, RoundingMode.HALF_UP);
                if (duration.longValue() > warn_duration) {
                    logger.warn("{}: {}[ms]", rule.getRuleClass(), duration);
                } else if (trace) {
                    logger.info("{}: {}[ms]", rule.getRuleClass(), duration);
                }
            }
        }

        return validationResult;
    }

    /**
     * Exceute bussines rule.
     *
     * @param object  - test object
     * @param context - context
     * @param rule    - rule definition
     * @return List<IValidationMessage> list of validation massages (not null)
     * @throws Exception
     */
    protected abstract List<ValidationMessage> check(Object object, ValidateContext context, BRuleDto rule) throws Exception;

}
