package org.fhbr.api.dao;

import java.time.LocalDate;

/**
 * Interface for XSD repository
 *
 * @author Dariusz Skrudlik
 * @version :  $, :  $
 * @created 30/09/2020
 */
public interface XsdRepositoryDao {

    byte[] getByNamespace(String namespace, LocalDate onDate);
    byte[] getByPublicId(String publicId, LocalDate onDate);

}
