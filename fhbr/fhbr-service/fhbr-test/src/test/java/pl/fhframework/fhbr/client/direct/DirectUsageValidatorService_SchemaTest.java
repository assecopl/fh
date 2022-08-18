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

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.fhframework.fhbr.api.config.ValidatorServiceConfig;
import pl.fhframework.fhbr.api.dao.XsdRepositoryDao;
import pl.fhframework.fhbr.api.factory.Factory;
import pl.fhframework.fhbr.api.model.BRuleSetDto;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationResult;
import pl.fhframework.fhbr.api.service.ValidatorService;
import pl.fhframework.fhbr.api.service.ValidatorServiceFactory;
import pl.fhframework.fhbr.dao.InMemoryBussinesRuleDao;
import pl.fhframework.fhbr.engine.ValidatorServiceFactoryImpl;
import pl.fhframework.fhbr.validator.schema.xsd.dao.ResourcePathXsdRepositoryDao;

import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 12/07/2022
 */
public class DirectUsageValidatorService_SchemaTest {

    private static Logger logger = LoggerFactory.getLogger(DirectUsageValidatorService_SchemaTest.class);

    private ValidatorService validatorService;

    @Before
    public void before() {
        ValidatorServiceFactory validatorFactory = new ValidatorServiceFactoryImpl();
        ValidatorServiceConfig config = new ValidatorServiceConfig();

        config.setModuleDaoFactory(() -> {
            InMemoryBussinesRuleDao moduleDao = new InMemoryBussinesRuleDao();

            BRuleSetDto moduleTEST = new BRuleSetDto();
            moduleTEST.setCode("TEST");
            moduleTEST.setScheamaValidator(true);
            moduleTEST.setNamespace("http://fhframework.pl/fh/fhbr-xsd/test_v1r0.xsd");
            moduleDao.getModules().put("TEST", moduleTEST);

            return moduleDao;
        });


        config.setXsdRepositoryDaoFactory(new Factory<XsdRepositoryDao>() {
            @Override
            public XsdRepositoryDao newInstance() {
                Map<String, String> schemeResourcesMap = new HashMap<>();
                schemeResourcesMap.put("http://fhframework.pl/fh/fhbr-xsd/test_v1r0.xsd", "/testset_1/test_v1r0.xsd");
                XsdRepositoryDao xsdRepositoryDao = new ResourcePathXsdRepositoryDao(schemeResourcesMap);
                return xsdRepositoryDao;
            }
        });

        validatorService = validatorFactory.newInstance(config);
    }

    @Test
    public void testValidMessage() throws IOException {

        //GIVEN
        byte[] xml = IOUtils.resourceToByteArray("/testset_1/test_ok.xml");

        ValidateObject<byte[]> validateObject = new ValidateObject();
        validateObject.setObject(xml);
        validateObject.setOnDate(LocalDate.now());

        //WHEN
        ValidationResult validationResult = validatorService.validate("TEST", null, validateObject);

        //THEN
        Assert.assertTrue(validationResult.getValid());

    }

    @Test
    public void testInValidMessage() throws IOException {

        //GIVEN
        byte[] xml_n = IOUtils.resourceToByteArray("/testset_1/test_nok_1.xml");

        ValidateObject<byte[]> validateObject = new ValidateObject();
        validateObject.setObject(xml_n);
        validateObject.setOnDate(LocalDate.now());

        //WHEN
        ValidationResult validationResult = validatorService.validate("TEST", null, validateObject);

        //THEN
        Assert.assertTrue(!validationResult.getValid());

    }
}