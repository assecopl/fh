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

import pl.fhframework.fhbr.api.checker.CheckerTypeService;
import pl.fhframework.fhbr.api.config.ValidatorServiceConfig;
import pl.fhframework.fhbr.api.dao.BRuleSetDao;
import pl.fhframework.fhbr.api.dao.XsdRepositoryDao;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.api.service.ValidatorService;
import pl.fhframework.fhbr.api.service.ValidatorServiceFactory;
import pl.fhframework.fhbr.api.service.impl.ValidationMessageFactoryImpl;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
public class ValidatorServiceFactoryImpl implements ValidatorServiceFactory {

    @Override
    public ValidatorService newInstance(ValidatorServiceConfig config) {

        //prepare messaga factory
        ValidationMessageFactory messageFactory = config.getMessageFactory() != null ? config.getMessageFactory() : new ValidationMessageFactoryImpl();

        //prepare registered checkers
        Map<String, CheckerTypeService> checkerTypeCollection = new HashMap<>();

        config.getCheckerTypeRegistry().forEach((type, checkerTypeServiceFactory) -> {
                    CheckerTypeService checkerTypeService = checkerTypeServiceFactory.newInstance();
                    if (checkerTypeService == null) {
                        throw new IllegalArgumentException("The checker factory '" + type + "' can't produced null object");
                    }
                    checkerTypeCollection.put(type, checkerTypeService);
                }
        );
//        if (checkerTypeCollection.isEmpty()) {
//            throw new IllegalArgumentException("At lest one 'checkerType' must be registered");
//        }

        //prepare module dao
        if (config.getModuleDaoFactory() == null) {
            throw new IllegalArgumentException("The 'moduleDaoFactory' can't be null");
        }
        BRuleSetDao bRuleSetDao = config.getModuleDaoFactory().newInstance();
        if (bRuleSetDao == null) {
            throw new IllegalArgumentException("The moduleDao factory can't produced null object");
        }
        XsdRepositoryDao xsdRepositoryDao = null;
        if (config.getXsdRepositoryDaoFactory() != null) {
            xsdRepositoryDao = config.getXsdRepositoryDaoFactory().newInstance();
            if (bRuleSetDao == null) {
                throw new IllegalArgumentException("The xsdRepositoryDao factory can't produced null object");
            }
        }

        //create the validator service
        return new ValidatorServiceImpl(messageFactory, bRuleSetDao, xsdRepositoryDao, checkerTypeCollection, config.getClock());
    }

}
