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

package pl.fhframework.fhbr.rule;

import pl.fhframework.fhbr.api.model.BRuleDto;
import pl.fhframework.fhbr.api.rule.ComplexRule;
import pl.fhframework.fhbr.api.service.ValidateContext;
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.example.TestObject;

import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 25/08/2022
 */
public class M2_Flow implements ComplexRule<TestObject> {

    @Override
    public List<ValidationMessage> check(TestObject object, ValidateContext context, BRuleDto rule) throws Exception {

        ValidateObject<TestObject> validateObject = new ValidateObject<>();
        validateObject.setObject(object);
        List<ValidationMessage> validationMessages = context.applyRules("M1", null, validateObject);

        return validationMessages;
    }

}
