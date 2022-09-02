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

package pl.fhframework.fhbr.api.service;


import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Wrapper for validated object.
 * <p>
 * Extension class may contain additional fields required during rule execution.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */


public class ValidateObject<O> {

    @Getter
    private final String requestId; //unique request id

    @Getter
    @Setter
    private LocalDate onDate; //validate on date (default current date)

    @Getter
    @Setter
    private O object; // the target object for check

    public ValidateObject() {
        this.requestId = UUID.randomUUID().toString();
    }

    public ValidateObject(O object) {
        this();
        this.object = object;
    }

    public ValidateObject(LocalDate onDate, O object) {
        this(object);
        this.onDate = onDate;
    }

    /**
     * Only if unique (recognizable) requestId - e.g. soap message id
     */
    public ValidateObject(String requestId, LocalDate onDate, O object) {
        this.requestId = requestId;
        this.onDate = onDate;
        this.object = object;
    }

}
