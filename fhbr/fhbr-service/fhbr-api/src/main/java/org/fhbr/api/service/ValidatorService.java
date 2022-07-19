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
package org.fhbr.api.service;

import org.fhbr.api.model.ValidationMessage;

import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 05/07/2022
 */
public interface ValidatorService {

    ValidationMessage validate(String moduleCode, Object object, Map<String, Object> context);
    
}