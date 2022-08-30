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

import pl.fhframework.fhbr.api.service.ValidationContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 26/08/2022
 */
public class R102_v1 implements R102 {

    @Override
    public List<ValidationMessage> execute(ValidationContext validationContext, int age, int shoeSizeNumber) {
//        public List<ValidationMessage> execute(BRuleDto dto, ValidationContext validationContext, int age, int shoeSizeNumber) {
//
        List<ValidationMessage> msgList = new ArrayList<>();

        if (age > shoeSizeNumber) {
            msgList.add(validationContext.createError("R102", "Age should be less than shoe size number (v1)"));
        }

        return msgList;
    }
}
