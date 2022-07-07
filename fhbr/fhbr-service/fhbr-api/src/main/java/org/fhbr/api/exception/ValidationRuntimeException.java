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

package org.fhbr.api.exception;

import lombok.Getter;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
@Getter
public class ValidationRuntimeException extends RuntimeException {

    private String moduleCode;
    private String ruleCode;

    public ValidationRuntimeException(String messageKey, String moduleCode, String ruleCode) {
        super(messageKey);
        this.moduleCode = moduleCode;
        this.ruleCode = ruleCode;
    }

    public ValidationRuntimeException(String messageKey, String moduleCode, String ruleCode, Throwable t) {
        super(messageKey, t);
        this.moduleCode = moduleCode;
        this.ruleCode = ruleCode;
    }

}
