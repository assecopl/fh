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

package pl.fhframework.fhbr.client.direct;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.fhbr.api.config.ValidatorServiceConfig;
import pl.fhframework.fhbr.api.model.BRuleCfgDto;
import pl.fhframework.fhbr.api.model.BRuleDefDto;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.api.service.ValidatorService;
import pl.fhframework.fhbr.api.service.ValidatorServiceFactory;
import pl.fhframework.fhbr.dao.InMemoryBussinesRuleDao;
import pl.fhframework.fhbr.engine.ValidatorServiceFactoryImpl;
import pl.fhframework.fhbr.example.TestObject;
import pl.fhframework.fhbr.validator.RuleClazzCheckerFactory;

import java.time.LocalDate;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 12/07/2022
 */
public class DirectUsageValidatorService_RuleTest {

    private static Logger logger = LoggerFactory.getLogger(DirectUsageValidatorService_RuleTest.class);

    @Test
    public void testValidatorService_Rule() {

        TestObject testObject = new TestObject();
        testObject.setActive(Boolean.TRUE);

        ValidatorServiceFactory validatorFactory = new ValidatorServiceFactoryImpl();
        ValidatorServiceConfig config = new ValidatorServiceConfig();

        /**
         {
         M1 {   R100_Flow;
         R101;
         }
         M2 { R201 }
         M5 { R500 }

         R102 v1 do 2022.03.31
         R102 v2 do 2022.04.01
         */
        config.setModuleDaoFactory(() -> {
            InMemoryBussinesRuleDao moduleDao = new InMemoryBussinesRuleDao();
            moduleDao.createRuleSet("M1");
            moduleDao.createRuleSet("M2");
            moduleDao.createRuleSet("M5");

            BRuleDto r100_flow = new BRuleDto();
            r100_flow.setConfig(new BRuleCfgDto());
            r100_flow.setDefinition(new BRuleDefDto());
            r100_flow.getConfig().setRuleCode("R100_Flow");
            r100_flow.getConfig().setActive(true);
            r100_flow.getConfig().setBusinessRuleCode("R100_Flow");
            r100_flow.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R100_Flow");
            r100_flow.getDefinition().setCheckerType("clazz");

            moduleDao.linkRule("M1", r100_flow);

            BRuleDto r101 = new BRuleDto();
            r101.setConfig(new BRuleCfgDto());
            r101.setDefinition(new BRuleDefDto());
            r101.getConfig().setRuleCode("R101");
            r101.getConfig().setActive(true);
            r101.getConfig().setBusinessRuleCode("R100");
            r101.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R101");
            r101.getDefinition().setCheckerType("clazz");
            r101.getConfig().setMessageKey("message.key.for.r101");

            moduleDao.linkRule("M1", r101);

            BRuleDto r102 = new BRuleDto();
            r102.setConfig(new BRuleCfgDto());
            r102.setDefinition(new BRuleDefDto());
            r102.getConfig().setRuleCode("R102_v1");
            r102.getConfig().setActive(true);
            r102.getConfig().setValidTo(LocalDate.parse("2022-03-30"));
            r102.getConfig().setBusinessRuleCode("R102");
            r102.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R102_v1");
            r102.getDefinition().setCheckerType("function");
            r102.getConfig().setMessageKey("message.key.for.r102_v1");

            moduleDao.addRule(r102);

            r102 = new BRuleDto();
            r102.setConfig(new BRuleCfgDto());
            r102.setDefinition(new BRuleDefDto());
            r102.getConfig().setRuleCode("R102_v2");
            r102.getConfig().setActive(true);
            r102.getConfig().setValidFrom(LocalDate.parse("2022-04-01"));
            r102.getConfig().setBusinessRuleCode("R102");
            r102.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R102_v2");
            r102.getDefinition().setCheckerType("function");
            r102.getConfig().setMessageKey("message.key.for.r102_v2");

            moduleDao.addRule(r102);

            BRuleDto r201 = new BRuleDto();
            r201.setConfig(new BRuleCfgDto());
            r201.setDefinition(new BRuleDefDto());
            r201.getConfig().setRuleCode("R201");
            r201.getConfig().setActive(true);
            r201.getConfig().setBusinessRuleCode("R201");
            r201.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R201");
            r201.getDefinition().setCheckerType("clazz");

            moduleDao.linkRule("M2", r201);

            BRuleDto r500 = new BRuleDto();
            r500.setConfig(new BRuleCfgDto());
            r500.setDefinition(new BRuleDefDto());
            r500.getConfig().setRuleCode("R500");
            r500.getConfig().setActive(true);
            r500.getConfig().setBusinessRuleCode("R500");
            r500.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R500");
            r500.getDefinition().setCheckerType("clazz");

            moduleDao.linkRule("M5", r500);

            return moduleDao;
        });

        config.getCheckerTypeRegistry().put("clazz", new RuleClazzCheckerFactory());
//        config.getCheckerTypeRegistry().put("groovy", new GroovyClazzCheckerFactory());
//        config.getCheckerTypeRegistry().put("other", new OtherClazzCheckerFactory());

        ValidatorService validatorService = validatorFactory.newInstance(config);

        ValidateObject<TestObject> validateObject = new ValidateObject();
        validateObject.setObject(testObject);
        validateObject.setOnDate(LocalDate.now());

        ValidationResult validationResult = validatorService.validate("M1", null, validateObject);

//        logger.info(validationResult.toString());
        logger.info("validationResult = " + new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(validationResult.toString())));
    }
}