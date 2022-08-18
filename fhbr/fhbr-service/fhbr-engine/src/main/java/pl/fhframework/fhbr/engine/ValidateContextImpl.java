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

import lombok.Getter;
import pl.fhframework.fhbr.api.service.ValidateContext;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationMessageFactory;
import pl.fhframework.fhbr.api.service.ValidationResult;

/**
 * Context for single validation request.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 18/08/2022
 */
public class ValidateContextImpl implements ValidateContext {

    @Getter
    private ValidationMessageFactory messageFactory;
    private final ValidatorServiceImpl validatorService;

    public ValidateContextImpl(ValidationMessageFactory messageFactory, ValidatorServiceImpl validatorService) {
        this.messageFactory = messageFactory;
        this.validatorService = validatorService;
    }

    public ValidationResult applyRules(String ruleSetCode, String phase, ValidateObject validateObject) {
        return validatorService.applyRules(this, ruleSetCode, phase, validateObject);
    }

}
