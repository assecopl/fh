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

package pl.fhframework.fhbr.engine;

import org.apache.commons.lang3.StringUtils;
import pl.fhframework.fhbr.api.checker.CheckerTypeService;
import pl.fhframework.fhbr.api.dao.ModuleDao;
import pl.fhframework.fhbr.api.exception.RuleValidationException;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.api.service.ValidatorService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
public class ValidatorServiceImpl implements ValidatorService {

    private final ModuleDao moduleDao;
    private final Map<String, CheckerTypeService> checkerTypeCollection;

    public ValidatorServiceImpl(ModuleDao moduleDao, Map<String, CheckerTypeService> checkerTypeCollection) {
        this.moduleDao = moduleDao;
        this.checkerTypeCollection = checkerTypeCollection;
    }

    @Override
    public ValidationResult validate(String moduleCode, ValidateObject object, Map<String, Object> context) {

        ValidationResult validationResult = new ValidationResult();

        List<BRuleDto> rules = moduleDao.findByModuleCode(moduleCode, "DEFAULT", true, object.getOnDate());

        rules.stream()
                .filter(r -> StringUtils.isNotBlank(r.getCheckerType()))
                .collect(Collectors.groupingBy(BRuleDto::getCheckerType))
                .forEach((ruleType, ruleTypeLists) -> {
                    CheckerTypeService validatorService = null;

                    try {
                        validatorService = prepareCheckerTypeService(ruleType);
                    } catch (Exception e) {
                        throw new RuleValidationException("fhbr.exception.createRuleValidatorService", moduleCode, null, e);
                    }

                    validatorService.validate(object, context, ruleTypeLists)
                            .getValidationResultMessages()
                            .stream().forEach(vm -> validationResult.addValidationMessage(vm));
                });

        return validationResult;

    }

    private CheckerTypeService prepareCheckerTypeService(String checkerType) {
        return checkerTypeCollection.get(checkerType);
    }

}
