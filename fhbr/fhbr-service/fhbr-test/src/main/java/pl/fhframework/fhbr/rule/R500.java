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
import lombok.Getter;
import pl.fhframework.fhbr.api.rule.SimpleRule;
import pl.fhframework.fhbr.api.service.ValidateContext;
import pl.fhframework.fhbr.api.service.ValidateObject;

import java.math.BigDecimal;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 11/07/2022
 */
public class R500 implements SimpleRule<R500.Data> {

    @Getter
    @AllArgsConstructor
    protected static class Data {
        Boolean active;
        BigDecimal amount;
    }

    public static ValidateObject prepare(Boolean active, BigDecimal amount) {
        return new ValidateObject(new Data(active, amount));
    }

    @Override
    public boolean isValid(Data targetObject, ValidateContext context) throws Exception {

        //IF active THEN amount must by greater than zero
        return targetObject.getActive() != null && (!targetObject.getActive() ||
                (targetObject.getActive() && targetObject.getAmount() != null
                        && (targetObject.getAmount().compareTo(BigDecimal.ZERO) > 1)
                )
        );
    }

}
