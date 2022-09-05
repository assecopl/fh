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

package pl.fhframework.fhbr.engine.checker;

import pl.fhframework.fhbr.api.service.ValidationContext;
import pl.fhframework.fhbr.api.service.ValidationMessage;

import java.util.List;
import java.util.function.BiFunction;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 29/08/2022
 */
public class RuleFunction<T> {

    private Class<T> clazz;
    //    private Function<T, List<ValidationMessage>> function;
    private BiFunction<ValidationContext, T, List<ValidationMessage>> biFunction;

//    public RuleFunction(Class<T> clazz, Function<T, List<ValidationMessage>> function) {
//        this.clazz = clazz;
//        this.function = function;
//    }

    public RuleFunction(Class<T> clazz, BiFunction<ValidationContext, T, List<ValidationMessage>> bifunction) {
        this.clazz = clazz;
        this.biFunction = bifunction;
    }

    public Class<T> getClazz() {
        return clazz;
    }

//    public Function<T, List<ValidationMessage>> getFunction() {
//        return function;
//    }

//    public boolean isBiFunction() {
//        return biFunction != null;
//    }

    public BiFunction<ValidationContext, T, List<ValidationMessage>> getBiFunction() {
        return biFunction;
    }
}
