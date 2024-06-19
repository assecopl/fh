package pl.fhframework.docs.change.service;

import org.springframework.web.client.HttpClientErrorException;

/**
 * Created by mateusz.zaremba
 */
public interface JiraSyncService {

    /**
     * Synchronize jira subttasks for given sprint task and save them into the changes.csv file.
     * @throws HttpClientErrorException - when for example 401 Unauthorized
     */
    void synchronizeJira();
}
