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
package pl.fhframework.fhbr.api.config;

import lombok.Getter;
import lombok.Setter;
import pl.fhframework.fhbr.api.checker.CheckerTypeServiceFactory;
import pl.fhframework.fhbr.api.dao.ModuleDao;
import pl.fhframework.fhbr.api.factory.Factory;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public class ValidatorServiceConfig {

    @Getter
    private Map<String, CheckerTypeServiceFactory> checkerTypeRegistry = new HashMap();

    public ValidatorServiceConfig addCheckerTypeFactory(String checkerType, CheckerTypeServiceFactory checkerTypeServiceFactory) {
        checkerTypeRegistry.put(checkerType, checkerTypeServiceFactory);
        return this;
    }

    private Map<String, String> feature = new HashMap();

    @Setter
    @Getter
    private Factory<ModuleDao> moduleDaoFactory;

    @Getter
    @Setter
    private ValidationMessageFactory messageFactory;

    public void setFeature(String featureKey, String featureValue) {
        feature.put(featureKey, featureValue);
    }


}
