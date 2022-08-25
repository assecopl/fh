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

        config.setModuleDaoFactory(() -> {
            InMemoryBussinesRuleDao moduleDao = new InMemoryBussinesRuleDao();
            moduleDao.getModules().put("M1", new BRuleSetDto());
            moduleDao.getModules().put("M2", new BRuleSetDto());

            List<BRuleDto> bRuleList_M1 = new ArrayList<>();

            BRuleDto r1 = new BRuleDto();
            r1.setConfig(new BRuleCfgDto());
            r1.setDefinition(new BRuleDefDto());
            r1.getConfig().setName("R1");
            r1.getConfig().setActive(true);
            r1.getConfig().setBusinessKey("R1");
            r1.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.R1");
            r1.getDefinition().setCheckerType("clazz");
            r1.getConfig().setMessageKey("message.key.for.r1");

            bRuleList_M1.add(r1);

            List<BRuleDto> bRuleList_M2 = new ArrayList<>();

            BRuleDto r2 = new BRuleDto();
            r2.setConfig(new BRuleCfgDto());
            r2.setDefinition(new BRuleDefDto());
            r2.getConfig().setName("R2_Flow");
            r2.getConfig().setActive(true);
            r2.getConfig().setBusinessKey("M2_Flow");
            r2.getDefinition().setRuleClassName("pl.fhframework.fhbr.rule.M2_Flow");
            r2.getDefinition().setCheckerType("clazz");

            bRuleList_M2.add(r2);


            moduleDao.getStorage().put("M1", bRuleList_M1);
            moduleDao.getStorage().put("M2", bRuleList_M2);
            return moduleDao;
        });

        config.getCheckerTypeRegistry().put("clazz", new RuleClazzCheckerFactory());
//        config.getCheckerTypeRegistry().put("groovy", new GroovyClazzCheckerFactory());
//        config.getCheckerTypeRegistry().put("other", new OtherClazzCheckerFactory());

        ValidatorService validatorService = validatorFactory.newInstance(config);

        ValidateObject<TestObject> validateObject = new ValidateObject();
        validateObject.setObject(testObject);
        validateObject.setOnDate(LocalDate.now());

        ValidationResult validationResult = validatorService.validate("M2", null, validateObject);

        logger.info(validationResult.toString());

    }
}