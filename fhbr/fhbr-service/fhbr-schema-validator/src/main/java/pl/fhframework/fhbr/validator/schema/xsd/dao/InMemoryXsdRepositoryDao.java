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

package pl.fhframework.fhbr.validator.schema.xsd.dao;

import pl.fhframework.fhbr.api.dao.XsdRepositoryDao;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 14/07/2022
 */
public class InMemoryXsdRepositoryDao implements XsdRepositoryDao {

    private final Map<String, byte[]> schemeContentMap;

    public InMemoryXsdRepositoryDao(Map<String, byte[]> schemeContentMap) {
        this.schemeContentMap = schemeContentMap;
    }

    @Override
    public byte[] getByNamespace(String namespace, LocalDate onDate) {
        return schemeContentMap.get(namespace);
    }

    @Override
    public byte[] getByPublicId(String publicId, LocalDate onDate) {
        return schemeContentMap.get(publicId);
    }
}
