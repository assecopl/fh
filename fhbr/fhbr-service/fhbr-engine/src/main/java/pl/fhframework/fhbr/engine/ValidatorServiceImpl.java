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
import pl.fhframework.fhbr.api.dao.BRuleSetDao;
import pl.fhframework.fhbr.api.dao.XsdRepositoryDao;
import pl.fhframework.fhbr.api.exception.RuleValidationException;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.model.BRuleSetDto;
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

    private final BRuleSetDao BRuleSetDao;
    private final XsdRepositoryDao xsdRepositoryDao;
    private final ValidationMessageFactory messageFactory;
    private final Map<String, CheckerTypeService> checkerTypeCollection;

    public ValidatorServiceImpl(ValidationMessageFactory messageFactory, BRuleSetDao BRuleSetDao, XsdRepositoryDao xsdRepositoryDao, Map<String, CheckerTypeService> checkerTypeCollection) {
        this.messageFactory = messageFactory;
        this.BRuleSetDao = BRuleSetDao;
        this.xsdRepositoryDao = xsdRepositoryDao;
        this.checkerTypeCollection = checkerTypeCollection;
    }

    @Override
    public ValidationResult validate(String ruleSetCode, String phase, ValidateObject validateObject) {

        ValidationResult validationResult = new ValidationResult();
        ValidateContext context = new ValidateContextImpl(messageFactory, this);

        BRuleSetDto bRuleSetDto = BRuleSetDao.findRuleSet(ruleSetCode, phase);
        if (bRuleSetDto == null) {
            ValidationMessage m = messageFactory.newInstance();
            m.setSeverity(ValidationMessageSeverity.ERROR);
            m.setMessage("pl.fhframework.fhbr.message.error.unknownModuleCode");
            validationResult.addValidationMessage(m);
        }

        if (bRuleSetDto.isScheamaValidator()) {
            new DaoXsdResolverFactory(xsdRepositoryDao, LocalDate.now());
            ValidationResult partialResult = new SchemaValidatorHelper(new DaoXsdResolverFactory(xsdRepositoryDao, LocalDate.now()), messageFactory)
                    .validate(bRuleSetDto.getNamespace(), (byte[]) validateObject.getObject());
            rewriteValidationMessage(partialResult, validationResult);
            if (!validationResult.getValid()) {
                return validationResult;
            }
        }

        ValidationResult applyRulesResult = applyRules(context, ruleSetCode, phase, validateObject);
        rewriteValidationMessage(applyRulesResult, validationResult);

        return validationResult;
    }

    /**
     * Apply rule set on target object.
     * Select rules by ruleSet/phase and execute verification with validateObject
     *
     * @param context
     * @param ruleSetCode
     * @param phase
     * @param validateObject
     * @return
     */
    ValidationResult applyRules(ValidateContext context, String ruleSetCode, String phase, ValidateObject validateObject) {

        ValidationResult validationResult = new ValidationResult();

        List<BRuleDto> rules = BRuleSetDao.findRules(ruleSetCode, phase, true, validateObject.getOnDate());

        rules.stream()
                .filter(r -> StringUtils.isNotBlank(r.getCheckerType()))
                .collect(Collectors.groupingBy(BRuleDto::getCheckerType))
                .forEach((ruleType, ruleTypeLists) -> {
                    CheckerTypeService validatorService = null;

                    try {
                        validatorService = prepareCheckerTypeService(ruleType);
                    } catch (Exception e) {
                        throw new RuleValidationException("fhbr.exception.createRuleValidatorService", ruleSetCode, null, e);
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
