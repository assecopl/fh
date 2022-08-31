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

import lombok.Data;
import lombok.Getter;
import lombok.Synchronized;
import pl.fhframework.fhbr.api.audit.AuditData;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 25/08/2022
 */

@Data
public class AuditPoint implements AuditData {

    @Getter
    protected final long startNanoTime;

    @Getter
    private final String name;

    private List<AuditPoint> auditData;

    public AuditPoint(String name) {
        this.name = name;
        this.startNanoTime = System.nanoTime();
    }

    public List<? extends AuditData> copyAuditData() {
        return auditData != null ? Collections.unmodifiableList(auditData) : null;
    }

    @Synchronized
    public void addAuditPoint(AuditPoint auditPoint) {
        if (auditData == null) {
            auditData = Collections.synchronizedList(new LinkedList<AuditPoint>());
        }
        auditData.add(auditPoint);
    }
}
