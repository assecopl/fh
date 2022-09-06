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

package pl.fhframework.fhbr.api.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 19/08/2022
 */
@Setter
@Getter
@Data
public class BRuleDefDto {

    private String checkerType; // service type must be registered

    private String ruleClassName;

    private String description;

    private String ruleExpression;

    private boolean compilationRequired;

    private String ruleSource;

    private byte[] ruleByteCode;

}
