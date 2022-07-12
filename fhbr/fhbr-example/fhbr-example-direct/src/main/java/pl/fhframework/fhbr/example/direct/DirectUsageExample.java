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

package pl.fhframework.fhbr.example.direct;

import pl.fhframework.fhbr.api.config.ValidatorServiceConfig;
import pl.fhframework.fhbr.api.dao.ModuleDao;
import pl.fhframework.fhbr.api.factory.Factory;
import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.api.service.ValidatorService;
import pl.fhframework.fhbr.api.service.ValidatorServiceFactory;
import pl.fhframework.fhbr.dao.InMemoryModuleDao;
import pl.fhframework.fhbr.engine.ValidatorServiceFactoryImpl;
import pl.fhframework.fhbr.example.TestObject;
import pl.fhframework.fhbr.rule.R1;
import pl.fhframework.fhbr.validator.RuleClazzCheckerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
public class DirectUsageExample {

    private ValidatorService validatorService;

    public ValidationResult validate() {

        TestObject testObject = new TestObject();
        testObject.setActive(Boolean.TRUE);

        ValidatorServiceFactory validatorFactory = new ValidatorServiceFactoryImpl();
        ValidatorServiceConfig config = new ValidatorServiceConfig();

        config.setModuleDaoFactory(new Factory<ModuleDao>() {
            @Override
            public ModuleDao newInstance() {
                InMemoryModuleDao moduleDao = new InMemoryModuleDao();
                List<BRuleDto> bRuleList = new ArrayList<>();

                BRuleDto r1 = new BRuleDto();
                r1.setName("R1");
                r1.setActive(true);
                r1.setBusinessKey("R1");
                r1.setRuleClass(R1.class.getName());
                r1.setCheckerType("clazz");

                bRuleList.add(r1);

                moduleDao.getStorage().put("M1", bRuleList);
                return moduleDao;
            }
        });

        config.getCheckerTypeRegistry().put("clazz", new RuleClazzCheckerFactory());
//        config.getCheckerTypeRegistry().put("groovy", new GroovyClazzCheckerFactory());
//        config.getCheckerTypeRegistry().put("other", new OtherClazzCheckerFactory());

        validatorService = validatorFactory.newInstance(config);

        Map<String, Object> context = new HashMap<>();
        ValidateObject<TestObject> validateObject = new ValidateObject();
        validateObject.setObject(testObject);
        validateObject.setOnDate(LocalDate.now());

        ValidationResult validationResult = validatorService.validate("M1", validateObject, context);

        return validationResult;

    }


}
