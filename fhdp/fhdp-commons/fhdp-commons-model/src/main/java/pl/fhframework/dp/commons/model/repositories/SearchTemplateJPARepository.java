package pl.fhframework.dp.commons.model.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.fhframework.dp.commons.model.entities.SearchTemplate;


@Repository
public interface SearchTemplateJPARepository extends JpaRepository<SearchTemplate, Long> {
}