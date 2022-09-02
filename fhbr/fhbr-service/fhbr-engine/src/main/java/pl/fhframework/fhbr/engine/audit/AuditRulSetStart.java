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

package pl.fhframework.fhbr.engine.audit;

import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 25/08/2022
 */
@Getter
public class AuditRulSetStart extends AuditPoint {

    private final String ruleSetName;
    private final LocalDateTime startTime = LocalDateTime.now();

    public AuditRulSetStart(String ruleSetName) {
        super("RuleStartStart");
        this.ruleSetName = ruleSetName;
    }

}
