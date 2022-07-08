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

package fhbr.validator.schema.xsd.resolver;

import fhbr.validator.schema.xsd.XsdResolverFactory;
import org.fhbr.api.dao.XsdRepositoryDao;
import org.w3c.dom.ls.LSResourceResolver;

import java.time.LocalDate;
import java.util.Map;

/**
 * @author Dariusz Skrudlik
 * @created 23/06/2020
 */
public class InMemoryXsdResolverFactory implements XsdResolverFactory {

    private final Map<String, byte[]> schemeContentMap;

    public InMemoryXsdResolverFactory(Map<String, byte[]> schemeContentMap) {
        this.schemeContentMap = schemeContentMap;
    }

    @Override
    public LSResourceResolver newInstance() {
        return new BaseXsdResolver(new XsdRepositoryDao() {

            @Override
            public byte[] getByNamespace(String namespace, LocalDate onDate) {
                return findResource(namespace);
            }

            @Override
            public byte[] getByPublicId(String publicId, LocalDate onDate) {
                return findResource(publicId);
            }

            public byte[] findResource(String name) {
                if (schemeContentMap.containsKey(name)) {
                    return schemeContentMap.get(name);
                }
                return null;
            }
        }, LocalDate.now());
    }
}
