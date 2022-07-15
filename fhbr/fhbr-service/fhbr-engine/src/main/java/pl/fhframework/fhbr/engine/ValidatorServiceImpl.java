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
import pl.fhframework.fhbr.api.dao.XsdRepositoryDao;
import pl.fhframework.fhbr.api.exception.RuleValidationException;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.model.ModuleDto;
import pl.fhframework.fhbr.api.service.*;
import pl.fhframework.fhbr.validator.schema.SchemaValidatorHelper;
import pl.fhframework.fhbr.validator.schema.xsd.resolver.DaoXsdResolverFactory;

import java.time.LocalDate;
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
    private final XsdRepositoryDao xsdRepositoryDao;
    private final ValidationMessageFactory messageFactory;
    private final Map<String, CheckerTypeService> checkerTypeCollection;

    public ValidatorServiceImpl(ValidationMessageFactory messageFactory, ModuleDao moduleDao, XsdRepositoryDao xsdRepositoryDao, Map<String, CheckerTypeService> checkerTypeCollection) {
        this.messageFactory = messageFactory;
        this.moduleDao = moduleDao;
        this.xsdRepositoryDao = xsdRepositoryDao;
        this.checkerTypeCollection = checkerTypeCollection;
    }

    @Override
    public ValidationResult validate(String moduleCode, String phase, ValidateObject validateObject, Map<String, Object> param) {

        ValidationResult validationResult = new ValidationResult();
        ValidateContext context = new ValidateContext(messageFactory, param);

        ModuleDto module = moduleDao.findModule(moduleCode, phase);
        if (module == null) {
            ValidationMessage m = messageFactory.newInstance();
            m.setSeverity(ValidationMessageSeverity.E);
            m.setMessage("pl.fhframework.fhbr.message.error.unknownModuleCode");
            validationResult.addValidationMessage(m);
        }
        if (module.isScheamaValidator()) {
            new DaoXsdResolverFactory(xsdRepositoryDao, LocalDate.now());
            ValidationResult partialResult = new SchemaValidatorHelper(new DaoXsdResolverFactory(xsdRepositoryDao, LocalDate.now()), messageFactory)
                    .validate(module.getNamespace(), (byte[]) validateObject.getObject());
            rewriteValidationMessage(partialResult, validationResult);
            if (!validationResult.getValid()) {
                return validationResult;
            }
        }
        List<BRuleDto> rules = moduleDao.findRules(moduleCode, phase, true, validateObject.getOnDate());

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

                    ValidationResult partialResult = validatorService.validate(validateObject.getObject(), context, ruleTypeLists);
                    rewriteValidationMessage(partialResult, validationResult);
                });

        return validationResult;

    }

    private CheckerTypeService prepareCheckerTypeService(String checkerType) {
        return checkerTypeCollection.get(checkerType);
    }

    private void rewriteValidationMessage(ValidationResult s, ValidationResult t) {
        s.getValidationResultMessages()
                .stream().forEach(vm -> t.addValidationMessage(vm));
    }

}
