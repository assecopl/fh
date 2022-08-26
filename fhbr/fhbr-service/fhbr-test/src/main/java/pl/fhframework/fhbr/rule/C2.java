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

import lombok.AllArgsConstructor;
import pl.fhframework.fhbr.api.rule.ConsumerRule;
import pl.fhframework.fhbr.api.service.ValidationContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 26/08/2022
 */

@AllArgsConstructor
public class C2 extends ConsumerRule {

    private BigDecimal grossMas;
    private BigDecimal netMas;

    @Override
    public List<ValidationMessage> apply(ValidationContext validationContext) {

        return null;
    }
}
