package pl.fhframework.fhPersistence.core.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Created by pawel.ruta on 2018-04-27.
 */
@Configuration
public class RepositoryInit {
    @Autowired(required = false)
    private List<Repository> repositories;
}
