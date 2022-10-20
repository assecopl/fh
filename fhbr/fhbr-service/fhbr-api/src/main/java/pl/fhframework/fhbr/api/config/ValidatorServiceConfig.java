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
import pl.fhframework.fhbr.api.dao.BRuleSetDao;
import pl.fhframework.fhbr.api.dao.XsdRepositoryDao;
import pl.fhframework.fhbr.api.factory.Factory;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;

import java.time.Clock;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public class ValidatorServiceConfig {

    @Getter
    private final Map<String, CheckerTypeServiceFactory> checkerTypeRegistry = new HashMap();

    public ValidatorServiceConfig addCheckerTypeFactory(String checkerType, CheckerTypeServiceFactory checkerTypeServiceFactory) {
        if (checkerType == null || checkerType.trim().length() == 0) {
            throw new IllegalArgumentException("The 'checkerType' can't be empty or null");
        }
        if (checkerTypeServiceFactory == null) {
            throw new IllegalArgumentException("The 'checkerTypeServiceFactory' can't be null");
        }
        checkerTypeRegistry.put(checkerType, checkerTypeServiceFactory);
        return this;
    }

    private final Map<String, String> feature = new HashMap();

    public void setFeature(String featureKey, String featureValue) {
        if (featureKey == null || featureKey.trim().length() == 0) {
            throw new IllegalArgumentException("The 'featureKey' can't be empty or null");
        }
        if (featureValue == null || featureValue.trim().length() == 0) {
            throw new IllegalArgumentException("The 'featureValue' can't be empty or null");
        }
        feature.put(featureKey, featureValue);
    }

    @Setter
    @Getter
    private Factory<BRuleSetDao> moduleDaoFactory;

    @Setter
    @Getter
    private Factory<XsdRepositoryDao> xsdRepositoryDaoFactory;

    @Getter
    @Setter
    private ValidationMessageFactory messageFactory;

    @Getter
    private Clock clock = Clock.systemDefaultZone();

    public void setClock(Clock clock) {
        if (clock == null) {
            throw new IllegalArgumentException("The 'clock' can't be null");
        }
        this.clock = clock;
    }
}
