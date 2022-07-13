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
import pl.fhframework.fhbr.api.dao.ModuleDao;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.api.service.ValidatorService;
import pl.fhframework.fhbr.api.service.ValidatorServiceFactory;
import pl.fhframework.fhbr.engine.result.ValidationMessageFactoryImpl;

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

        ValidationMessageFactory messageFactory = config.getMessageFactory() != null ? config.getMessageFactory() : new ValidationMessageFactoryImpl();

        Map<String, CheckerTypeService> checkerTypeCollection = new HashMap<>();

        config.getCheckerTypeRegistry().forEach((type, checkerTypeServiceFactory) ->
                checkerTypeCollection.put(type, checkerTypeServiceFactory.newInstance())
        );
        ModuleDao moduleDao = config.getModuleDaoFactory().newInstance();

        return new ValidatorServiceImpl(messageFactory, moduleDao, checkerTypeCollection);
    }

}
