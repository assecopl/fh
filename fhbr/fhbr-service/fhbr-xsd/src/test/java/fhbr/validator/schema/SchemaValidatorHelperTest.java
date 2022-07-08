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

package fhbr.validator.schema;

import org.apache.commons.io.IOUtils;
import org.fhbr.api.model.ValidationResult;
import org.fhbr.api.model.ValidationResultStatus;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

/**
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 08/07/2022
 */
public class SchemaValidatorHelperTest {

    @Test
    public void validate() throws IOException {

        //GIVEN
        byte[] xml = IOUtils.resourceToByteArray("/testset_1/test_ok.xml");

        //WHEN
        ValidationResult validationResult = new SchemaValidatorHelper().validateXSD(xml);

        //THEN
        Assert.assertTrue(ValidationResultStatus.NOK.equals(validationResult.getValidationResultStatus()));
        Assert.assertTrue(validationResult.getValidationResultMessages().size() == 1);


    }

    @Test
    public void validateXSD() throws IOException {

        //GIVEN
        byte[] xsd = IOUtils.resourceToByteArray("/testset_1/test_v1r0.xsd");

        //WHEN
        ValidationResult validationResult = new SchemaValidatorHelper().validateXSD(xsd);

        //THEN
        Assert.assertTrue(ValidationResultStatus.OK.equals(validationResult.getValidationResultStatus()));
    }
}