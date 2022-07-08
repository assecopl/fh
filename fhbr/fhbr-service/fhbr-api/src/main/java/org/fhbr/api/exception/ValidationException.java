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

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
@Getter
public class ValidationException extends RuntimeException {

    private String msgKey;
    private Map<String,String> msgProperties;

    public ValidationException(String message, String msgKey, Map<String,String> msgProperties) {
        super(message);
        this.msgKey = msgKey;
        this.msgProperties = msgProperties;
    }

    public ValidationException(String message, String msgKey, Map<String,String> msgProperties, Throwable t) {
        super(message, t);
        this.msgKey = msgKey;
        this.msgProperties = msgProperties;
    }

    public ValidationException(String msgKey, Map<String,String> msgProperties) {
        this.msgKey = msgKey;
        this.msgProperties = msgProperties;
    }

    public ValidationException(String msgKey, String key, String value) {
        this.msgKey = msgKey;
        this.msgProperties = new HashMap<>();
        this.msgProperties.put(key,value);
    }
}
