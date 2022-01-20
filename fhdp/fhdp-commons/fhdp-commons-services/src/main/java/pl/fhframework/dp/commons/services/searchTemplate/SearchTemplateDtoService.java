package pl.fhframework.dp.commons.services.searchTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.fhframework.dp.commons.model.entities.SearchTemplate;
import pl.fhframework.dp.commons.els.repositories.SearchTemplateESRepository;
import pl.fhframework.dp.commons.model.repositories.SearchTemplateJPARepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDefinition;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDto;
import pl.fhframework.dp.transport.searchtemplate.SearchTemplateDtoQuery;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service("searchTemplateDtoService")
@Slf4j
public class SearchTemplateDtoService extends GenericDtoService<Long, SearchTemplateDto, SearchTemplateDto, SearchTemplateDtoQuery, SearchTemplate> {

    @Autowired
    SearchTemplateJPARepository searchTemplateRepository;
    @Autowired
    SearchTemplateESRepository searchTemplateESRepository;

    public SearchTemplateDtoService() {
        super(SearchTemplateDto.class, SearchTemplateDto.class, SearchTemplate.class);
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, SearchTemplateDtoQuery query) {
        if (query.getId() != null) {
            builder.must(QueryBuilders.matchQuery("id", query.getId()));
        }
        if (query.getTemplateName() != null) {
            builder.must(QueryBuilders.matchQuery("templateName", query.getTemplateName()));
        }
        if (query.getComponentName() != null) {
            builder.must(QueryBuilders.matchQuery("componentName", query.getComponentName()));
        }
        if (!query.getUserName().isEmpty()) {
            builder.must(QueryBuilders.matchQuery("userName", query.getUserName()));
        }
        return builder;
    }

    @Override
    public SearchTemplateDto getDto(Long key) {
        Optional<SearchTemplate> gOpt = searchTemplateRepository.findById(key);
        SearchTemplateDto ret = null;
        if (gOpt.isPresent()) {
            ret = mapEntityToDto(gOpt.get(), true);
            ret.setSearchTemplateDefinitions(BeanConversionUtil.getFromJson(gOpt.get().getTemplateDefinitionJson(),  new TypeReference<List<SearchTemplateDefinition>>() { }));
        }
        return ret;
    }

    @Override
    @Transactional
    public Long persistDto(SearchTemplateDto searchTemplateDto) {
            SearchTemplate searchTemplate = mapDtoToEntity(searchTemplateDto, true);
            searchTemplate.setTemplateDefinitionJson(BeanConversionUtil.toJson(searchTemplateDto.getSearchTemplateDefinitions()));
            SearchTemplate ret = searchTemplateRepository.saveAndFlush(searchTemplate);
            searchTemplateDto.setId(ret.getId());
            searchTemplateDto.setVersion(ret.getVersion());
            searchTemplateESRepository.save(searchTemplateDto);
  //          reindex();
            return ret.getId();
    }

    @Override
    @Transactional
    public void deleteDto(Long key) {
        searchTemplateRepository.deleteById(key);
        searchTemplateESRepository.deleteById(key);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void reindex() {
        log.info("Deleting index {}", getIndexName());
        elasticsearchTemplate.indexOps(SearchTemplateDto.class).delete();
        List<SearchTemplate> searchCriteria = searchTemplateRepository.findAll();
        List<SearchTemplateDto> dtos = mapEntityToDtoForBulkReindex(searchCriteria);
        searchTemplateESRepository.saveAll(dtos);
    }

    @Override
    public JpaRepository<SearchTemplate, Long> getJpaRepository() {
        return searchTemplateRepository;
    }

    @Override
    protected List<SearchTemplateDto> mapEntityToDtoForBulkReindex(List<SearchTemplate> entities) {
        List<SearchTemplateDto> dtos = new ArrayList<>();
        for (SearchTemplate entity : entities) {
            SearchTemplateDto searchCriteriaDto = mapEntityToDto(entity, true);

            List<SearchTemplateDefinition> filters =  BeanConversionUtil.getFromJson(entity.getTemplateDefinitionJson(),  new TypeReference<List<SearchTemplateDefinition>>() { });
            searchCriteriaDto.setSearchTemplateDefinitions(filters);
            dtos.add(searchCriteriaDto);
        }
        return dtos;
    }
}
