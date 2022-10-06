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
import pl.fhframework.fhbr.api.exception.ValidationException;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.model.BRuleSetDto;
import pl.fhframework.fhbr.api.service.*;
import pl.fhframework.fhbr.engine.checker.RuleFunction;
import pl.fhframework.fhbr.engine.context.DelegateValidationContextImpl;
import pl.fhframework.fhbr.engine.context.InternalValidationContext;
import pl.fhframework.fhbr.engine.context.MainValidationContextImpl;
import pl.fhframework.fhbr.engine.factory.RuleInstanceFactoryImpl;
import pl.fhframework.fhbr.validator.schema.SchemaValidatorHelper;
import pl.fhframework.fhbr.validator.schema.xsd.resolver.DaoXsdResolverFactory;

import java.time.Clock;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
public class ValidatorServiceImpl implements ValidatorService {

    private final BRuleSetDao bRuleSetDao;
    private final XsdRepositoryDao xsdRepositoryDao;
    private final ValidationMessageFactory messageFactory;
    private final Map<String, CheckerTypeService> checkerTypeCollection;
    private final Clock internalClock;
    private final RuleInstanceFactoryImpl ruleInstanceFactory = new RuleInstanceFactoryImpl();


    public ValidatorServiceImpl(ValidationMessageFactory messageFactory, BRuleSetDao bRuleSetDao, XsdRepositoryDao xsdRepositoryDao, Map<String, CheckerTypeService> checkerTypeCollection, Clock clock) {
        this.messageFactory = messageFactory;
        this.bRuleSetDao = bRuleSetDao;
        this.xsdRepositoryDao = xsdRepositoryDao;
        this.checkerTypeCollection = checkerTypeCollection;
        this.internalClock = clock;
    }

    @Override
    public ValidationResult validate(String ruleSetCode, ValidateObject validateObject) {

        InternalValidationContext context = new DelegateValidationContextImpl(
                new MainValidationContextImpl(messageFactory, this, ruleSetCode, validateObject.getOnDate(), internalClock)
        );
        ValidationResult validationResult = new ValidationResult(context.getAuditPoint());

        try {
            BRuleSetDto bRuleSetDto = bRuleSetDao.findRuleSet(ruleSetCode);
            if (bRuleSetDto == null) {
                throw new ValidationException("fhbr.exception.unknownRuleSetCode", ruleSetCode);
            }

            if (bRuleSetDto.isSchemaValidator()) {
                new DaoXsdResolverFactory(xsdRepositoryDao, context.getInitialOnDate());
                ValidationResult partialResult = new SchemaValidatorHelper(new DaoXsdResolverFactory(xsdRepositoryDao, LocalDate.now()), messageFactory)
                        .validate(bRuleSetDto.getSchemaNamespace(), (byte[]) validateObject.getObject());
                rewriteValidationMessage(partialResult.getValidationResultMessages(), validationResult);
                if (!validationResult.getValid()) {
                    return validationResult;
                }
            }

            List<ValidationMessage> validationMessages = applyRuleSet(context, ruleSetCode, validateObject);
            rewriteValidationMessage(validationMessages, validationResult);

            context.getAuditPoint().finish();
        } catch (Throwable e) {
            if (!(e instanceof ValidationException)) {
                e = new ValidationException(ruleSetCode, e);
            }
            ((ValidationException) e).logError();
            throw (ValidationException) e;
        }

        return validationResult;
    }

    /**
     * Apply rule set on target object.
     * Select rules by ruleSet/phase and execute verification with validateObject
     *
     * @param context
     * @param ruleSetCode
     * @param validateObject
     * @return
     */
    public List<ValidationMessage> applyRuleSet(InternalValidationContext context, String ruleSetCode, ValidateObject validateObject) {
        List<BRuleDto> rules = bRuleSetDao.findRuleSetRules(ruleSetCode, true, context.getInitialOnDate());
        return applyRules(context, validateObject, rules);
    }

//    public List<ValidationMessage> applyRule(ValidationContext context, String ruleCode, String phase, ValidateObject validateObject) {
//        List<BRuleDto> rules = BRuleSetDao.findRule(ruleCode, validateObject.getOnDate());
//        return applyRules(context, validateObject, rules);
//    }

    List<ValidationMessage> applyRules(InternalValidationContext context, ValidateObject validateObject, List<BRuleDto> rules) {

        List<ValidationMessage> validationMessages = Collections.synchronizedList(new LinkedList<>());

        rules.stream()
                .filter(r -> StringUtils.isNotBlank(r.getDefinition().getCheckerType()))
                .collect(Collectors.groupingBy(bRuleDto -> bRuleDto.getDefinition().getCheckerType()))
                .forEach((ruleType, ruleTypeLists) -> {
                    CheckerTypeService checkerTypeService = null;

                    checkerTypeService = prepareCheckerTypeService(ruleType);

                    ValidationResult partialResult = checkerTypeService.validate(validateObject.getObject(), context, ruleTypeLists);
                    validationMessages.addAll(partialResult.getValidationResultMessages());
                });

        return validationMessages;
    }

    @Override
    public boolean canIRun(String businessRuleCode, LocalDate onDay) {
        return bRuleSetDao.findActiveRule(businessRuleCode, onDay) != null;
    }

    public <T> List<ValidationMessage> applyNow(InternalValidationContext validationContext, List<RuleFunction<? extends Class<?>>> ruleFunctionList) {

        List<ValidationMessage> result = Collections.synchronizedList(new ArrayList<>());
        //group by businessRuleCode
        //businessRuleCode => interface class. getSimpleName
        Map<String, List<RuleFunction<? extends Class<?>>>> functionRuleMap = ruleFunctionList.stream().collect(Collectors.groupingBy(ruleFunction -> ruleFunction.getClazz().getSimpleName()));

        //find rules configuration
        List<BRuleDto> rules = bRuleSetDao.findActiveRules(new ArrayList<>(functionRuleMap.keySet()), validationContext.getInitialOnDate());

        //TODO: group by priority and run parallel

        rules.parallelStream().forEach(rule -> {
            functionRuleMap.get(rule.getConfig().getBusinessRuleCode()).parallelStream().forEach(f -> {
                result.addAll(applyNow(validationContext, rule, f).get());
            });
        });

        return result;
    }

    //    public <T> List<ValidationMessage> applyNow(ValidationContext validationContext, Class<T> clazz, Function<T, List<ValidationMessage>> function) {
    public <T> List<ValidationMessage> applyNow(InternalValidationContext validationContext, RuleFunction<T> ruleFunction) {
        BRuleDto bRuleDto = bRuleSetDao.findActiveRule(ruleFunction.getClazz().getSimpleName(), validationContext.getInitialOnDate());

        return applyNow(validationContext, bRuleDto, ruleFunction).get();
    }

    //    private <T> Supplier<List<ValidationMessage>> applyNow(ValidationContext validationContext, BRuleDto bRuleDto, Function<T, List<ValidationMessage>> function) {
    private <T> Supplier<List<ValidationMessage>> applyNow(InternalValidationContext validationContext, BRuleDto bRuleDto, RuleFunction<T> ruleFunction) {
        return () -> {
            if (bRuleDto != null) {
                T rule = (T) ruleInstanceFactory.getRuleInstance(bRuleDto);
                //add validatorContext decorator
                ValidationContextImpl ruleContext = new ValidationContextImpl(validationContext, bRuleDto.getConfig());
                try {
                    return ruleFunction.getBiFunction().apply(ruleContext, rule);
                } finally {
                    ruleContext.getAuditPoint().finish();
                }
            }
            return Collections.emptyList();
        };
    }

    private CheckerTypeService prepareCheckerTypeService(String checkerType) {
        try {
            return checkerTypeCollection.get(checkerType);
        } catch (Exception e) {
            throw new ValidationException("fhbr.exception.prepareCheckerTypeService", e, checkerType);
        }
    }

    private void rewriteValidationMessage(List<ValidationMessage> validationMessages, ValidationResult t) {
        validationMessages
                .stream().forEach(vm -> t.addValidationMessage(vm));
    }

}
