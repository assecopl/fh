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
import pl.fhframework.fhbr.api.model.BRuleSetDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.api.service.ValidatorService;
import pl.fhframework.fhbr.api.service.ValidatorServiceFactory;
import pl.fhframework.fhbr.dao.InMemoryBussinesRuleDao;
import pl.fhframework.fhbr.engine.ValidatorServiceFactoryImpl;
import pl.fhframework.fhbr.example.TestObject;
import pl.fhframework.fhbr.validator.RuleClazzCheckerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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
         M1 { R100_Flow; R101 }
         M2 { R201 }
         M5 { R500 }
         */
        config.setModuleDaoFactory(() -> {
            InMemoryBussinesRuleDao moduleDao = new InMemoryBussinesRuleDao();
            moduleDao.getRuleSets().put("M1", new BRuleSetDto());
            moduleDao.getRuleSets().put("M2", new BRuleSetDto());
            moduleDao.getRuleSets().put("M5", new BRuleSetDto());

            List<BRuleDto> bRuleList_M1 = new ArrayList<>();

            BRuleDto r100_flow = new BRuleDto();
            r100_flow.setConfig(new BRuleCfgDto());
            r100_flow.setDefinition(new BRuleDefDto());
            r100_flow.getConfig().setRuleCode("R100_Flow");
            r100_flow.getConfig().setActive(true);
            r100_flow.getConfig().setBusinessKey("R100_Flow");
            r100_flow.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R100_Flow");
            r100_flow.getDefinition().setCheckerType("clazz");

            bRuleList_M1.add(r100_flow);

            BRuleDto r100 = new BRuleDto();
            r100.setConfig(new BRuleCfgDto());
            r100.setDefinition(new BRuleDefDto());
            r100.getConfig().setRuleCode("R101");
            r100.getConfig().setActive(true);
            r100.getConfig().setBusinessKey("R100");
            r100.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R101");
            r100.getDefinition().setCheckerType("clazz");
            r100.getConfig().setMessageKey("message.key.for.r101");

            bRuleList_M1.add(r100);

            List<BRuleDto> bRuleList_M2 = new ArrayList<>();

            BRuleDto r201 = new BRuleDto();
            r201.setConfig(new BRuleCfgDto());
            r201.setDefinition(new BRuleDefDto());
            r201.getConfig().setRuleCode("R201");
            r201.getConfig().setActive(true);
            r201.getConfig().setBusinessKey("R201");
            r201.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R201");
            r201.getDefinition().setCheckerType("clazz");

            bRuleList_M2.add(r201);

            List<BRuleDto> bRuleList_M5 = new ArrayList<>();

            BRuleDto r500 = new BRuleDto();
            r500.setConfig(new BRuleCfgDto());
            r500.setDefinition(new BRuleDefDto());
            r500.getConfig().setRuleCode("R500");
            r500.getConfig().setActive(true);
            r500.getConfig().setBusinessKey("R500");
            r500.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R500");
            r500.getDefinition().setCheckerType("clazz");

            bRuleList_M5.add(r500);

            moduleDao.getRuleSetsRules().put("M1", bRuleList_M1);
            moduleDao.getRuleSetsRules().put("M2", bRuleList_M2);
            moduleDao.getRuleSetsRules().put("M5", bRuleList_M5);
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