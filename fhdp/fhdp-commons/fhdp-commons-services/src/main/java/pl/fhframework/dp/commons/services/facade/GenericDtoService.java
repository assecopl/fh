package pl.fhframework.dp.commons.services.facade;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.commons.camunda.ElasticSearchConfig;
import pl.fhframework.dp.commons.utils.conversion.BeanConversionUtil;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;
import pl.fhframework.dp.transport.service.IDtoService;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 25.08.2019
 */
@Service
@Slf4j
public abstract class GenericDtoService<ID,
        DTO extends IPersistentObject,
        LIST extends IPersistentObject,
        QUERY extends BaseDtoQuery,
        ENTITY> implements IDtoService<ID, DTO , LIST , QUERY> {

    @Autowired
    ElasticSearchConfig elasticSearchConfig;
    @Autowired
    protected ElasticsearchOperations elasticsearchTemplate;
    private Class<LIST> listClazz;
    private Class<DTO> dtoClazz;
    private Class<ENTITY> entityClazz;


    public GenericDtoService(Class<LIST> listClass, Class<DTO> dtoClazz, Class<ENTITY> entityClazz) {
        this.listClazz = listClass;
        this.dtoClazz = dtoClazz;
        this.entityClazz = entityClazz;
    }

    @Override
    public List<LIST> listDto(QUERY query) {
        BoolQueryBuilder queryBuilder = createQueryBuilderInternal(query);
        NativeSearchQueryBuilder searchQueryBuilder = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder);
        if(query.getFirstRow() != null && query.getSize() != null) {
            Sort sort ;
            if(query.getSortProperty() != null) {
                sort = Sort.by((query.getAscending() != null && !query.getAscending())?Sort.Direction.DESC : Sort.Direction.ASC, query.getSortProperty());
            } else {
                sort = Sort.by(Sort.Direction.ASC, "id");
            }
            Pageable pageable;
            // bez sortowania jesli SortProperty = "0"
            if (query.getSortProperty() != null && query.getSortProperty().equals("0")) pageable = PageRequest.of(query.getFirstRow()/query.getSize(), query.getSize());
            else pageable = PageRequest.of(query.getFirstRow()/query.getSize(), query.getSize(), sort);
            searchQueryBuilder = searchQueryBuilder.withPageable(pageable);
        }
        NativeSearchQuery searchQuery = searchQueryBuilder.build();
        List<LIST> list = new ArrayList<>();
        try {
            IndexCoordinates indexCoordinates = elasticsearchTemplate.getIndexCoordinatesFor(listClazz);
            SearchHits<LIST> res = elasticsearchTemplate.search(searchQuery, listClazz, indexCoordinates);
            list = res.getSearchHits().stream().map(SearchHit::getContent).collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("{}", ExceptionUtils.getStackTrace(e));
        }
        return list;
    }

    private BoolQueryBuilder createQueryBuilderInternal(QUERY query) {
        BoolQueryBuilder ret = QueryBuilders.boolQuery();
        if(query.getTextSearch() != null) {
            String txt = (query.isWholeWordsOnly()? "": "*") +
                    escapeSpecialCharacters(query.getTextSearch() +
                            (query.isWholeWordsOnly()? "": "*"));
            ret.must(QueryBuilders.queryStringQuery(txt)
                    .analyzeWildcard(true)
                    .allowLeadingWildcard(true));
        }
        ret = extendQueryBuilder(ret, query);
        return ret;
    }
    //TODO: English
    /**
     *
     * Metoda rozszerzająca podstawowy queryBuilder, zapewniający wyszukiwanie pełnotekstowe.
     * @param builder
     * @param query
     * @return
     */
    protected abstract BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, QUERY query);

    @Override
    public Long listCount(QUERY query) {
        BoolQueryBuilder queryBuilder = createQueryBuilderInternal(query);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(queryBuilder)
                .build();
        IndexCoordinates indexCoordinates = elasticsearchTemplate.getIndexCoordinatesFor(listClazz);
        return elasticsearchTemplate.count(searchQuery, indexCoordinates);
    }

    /**
     * Metoda zwracająca nazwę indeksu z klasy DTO
     * @return
     */
    protected String getIndexName() {
        Document annotation = listClazz.getAnnotation(Document.class);
        try {
            Method method = annotation.annotationType().getDeclaredMethod("indexName");
            String value = (String) method.invoke(annotation, (Object[])null);
            return value.replace("#{@indexNamePrefix}", elasticSearchConfig.getIndexNamePrefix());
        } catch (Exception e) {
            log.error("{}{}", e.getMessage(), e);
        }
        return null;
    }

    public DTO mapEntityToDto(ENTITY entity, boolean withoutNulls) {
        return (DTO) BeanConversionUtil.mapObject(entity, withoutNulls, dtoClazz);
    }

    public ENTITY mapDtoToEntity(DTO dto, boolean withoutNulls) {
        return (ENTITY) BeanConversionUtil.mapObject(dto, withoutNulls, entityClazz);
    }

    /**
     * Metoda pozwala na odtworzenie więzów w konkretnej encji
     * @param ret
     */
    protected void enrichEntity(ENTITY ret) {

    }

    private static Map<String, String> getEscapeCharsMap() {
        Map<String, String> replacementMap = new HashMap<>();
        replacementMap.put("+", "\\+");
        replacementMap.put("-", "\\-");
        replacementMap.put("=", "\\=");
        replacementMap.put("&&", "\\&&");
        replacementMap.put("||", "\\||");
        replacementMap.put("!", "\\!");
        replacementMap.put("(", "\\(");
        replacementMap.put(")", "\\)");
        replacementMap.put("{", "\\{");
        replacementMap.put("}", "\\}");
        replacementMap.put("[", "\\[");
        replacementMap.put("]", "\\]");
        replacementMap.put("^", "\\^");
        replacementMap.put("\"", "\\\"");
        replacementMap.put("~", "\\~");
        replacementMap.put(":", "\\:");
        replacementMap.put("/", "\\/");
        return replacementMap;
    }

    public String escapeSpecialCharacters(String input){
        String ret = input.replace( "\\", "\\\\" );
        int charsCount = getEscapeCharsMap().keySet().size();
        String[] chars = Arrays.copyOf(getEscapeCharsMap().keySet().toArray(), charsCount, String[].class);
        String[] escapedChars = Arrays.copyOf(getEscapeCharsMap().values().toArray(), charsCount, String[].class);
        ret = StringUtils.replaceEach(ret, chars, escapedChars);
        ret = ret.toLowerCase();
        log.debug("*** escapeSpecialCharacters from {} to {}", input, ret);
        return ret;
    }

    public List<String> escapeSpecialCharacters(List<String> arg){
        return arg.stream().map(item -> escapeSpecialCharacters(item)).collect(Collectors.toList());
    }

    public JpaRepository<ENTITY, ID> getJpaRepository(){
        return null;
    }

    public Slice<ENTITY> reindexPage(Pageable pageable, Class dtoClass, String indexName, String indexType) {
        Slice<ENTITY> slice = getJpaRepository().findAll(pageable);
        List<DTO> dtoList = mapEntityToDtoForBulkReindex(slice.getContent());
        List<IndexQuery> queries = new ArrayList<>();

        for (DTO dto : dtoList) {
            IndexQuery indexQuery = new IndexQuery();
            indexQuery.setId(dto.getId().toString());
            indexQuery.setObject(dto);
//            if (StringUtils.isNotBlank(indexType))
//                indexQuery.setType(getDtoEsIndexType(dtoClass));
            queries.add(indexQuery);
        }
        IndexCoordinates indexCoordinates = elasticsearchTemplate.getIndexCoordinatesFor(listClazz);
        elasticsearchTemplate.bulkIndex(queries, indexCoordinates);
        queries.clear();
        return slice;
    }

    protected List<DTO> mapEntityToDtoForBulkReindex(List<ENTITY> entities) {
        return entities.stream()
                .map(entity -> mapEntityToDto(entity, false))
                .collect(Collectors.toList()
                );
    }

    public long getTotalRecordsCount(){
        return getJpaRepository().count();
    }

    public  String getDtoEsIndexName(Class<DTO> cls) {
        if (cls.isAnnotationPresent(Document.class)) {
            Document doc =  cls.getAnnotation(Document.class);
            return doc.indexName();
        }
        return null;
    }

//    public  String getDtoEsIndexType(Class<DTO> cls) {
//        if (cls.isAnnotationPresent(Document.class)) {
//            Document doc =  cls.getAnnotation(Document.class);
//            return doc.type();
//        }
//        return null;
//    }
}
