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

import java.io.Serializable;
import java.time.LocalDate;

/**
 * Rule configuration.
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 06/07/2022
 */
@Setter
@Getter
@Data
public class BRuleCfgDto implements Serializable {

    @Getter
    private String ruleCode; //required, unique rule code - businessRuleCode + variant eg: R602_A

    private String version; // on period defined by validFrom - validTo only one version with businessRuleCode is allowed

    private String businessRuleCode; //eg: R602

    private boolean active;

    private String name;

    private String messageKey;

    private String message;

    private String pointer;

    private int callOrder; // lower order -> higher priority (first run)

    private String severity;

    private LocalDate validFrom; // start date of the period validity

    private LocalDate validTo; // end date of the period validity

    private boolean critical;

    private String phase; //  phase eg. DEFAULT


}
