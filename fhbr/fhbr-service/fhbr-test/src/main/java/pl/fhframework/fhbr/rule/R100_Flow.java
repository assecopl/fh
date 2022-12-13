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
import pl.fhframework.fhbr.api.service.ValidateObject;
import pl.fhframework.fhbr.api.service.ValidationContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;
import pl.fhframework.fhbr.example.TestObject;

import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 25/08/2022
 */
public class R100_Flow implements ComplexRule<TestObject> {

    @Override
    public List<ValidationMessage> check(TestObject targetObject, ValidationContext context, BRuleDto rule) throws Exception {

        ValidateObject<TestObject> validateObject = new ValidateObject<>();
        validateObject.setObject(targetObject);

        List<ValidationMessage> validationMessages = context.applyRuleSet("M2", validateObject);

//                context.subscribeRule("R500", R500.prepare(targetObject.getActive(), targetObject.getAmount()))

        context.subscribeRule(R102.class, (contex, r102) -> r102.execute(contex, 5, 10));

        validationMessages.addAll(context.runSubscribedRules());

        return validationMessages;
    }

}
