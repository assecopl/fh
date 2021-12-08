package pl.fhframework.dp.commons.services.parameters;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SpanNearQueryBuilder;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.model.repositories.SubstantiveParametersESRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.transport.dto.parameters.*;
import pl.fhframework.dp.transport.service.ISubstantiveParametersService;
import pl.fhframework.dp.transport.dto.parameters.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service("substantiveParametersService")
public class SubstantiveParametersService
    extends GenericDtoService<String, SubstantiveParametersDto, SubstantiveParametersDto, SubstantiveParametersDtoQuery, SubstantiveParametersDto>
    implements ISubstantiveParametersService {

    @Autowired
    SubstantiveParametersESRepository substantiveParametersESRepository;
    @Autowired
    SubstantiveParametersTagDtoService substantiveParametersTagDtoService;

    private ISubstantiveParametersParameterDefinition substantiveParametersParameterDefinition;

    public SubstantiveParametersService() {
        super(SubstantiveParametersDto.class, SubstantiveParametersDto.class, SubstantiveParametersDto.class);
    }

    public SubstantiveParametersService(ISubstantiveParametersParameterDefinition substantiveParametersParameterDefinition) {
        super(SubstantiveParametersDto.class, SubstantiveParametersDto.class, SubstantiveParametersDto.class);
        this.substantiveParametersParameterDefinition = substantiveParametersParameterDefinition;
    }

    /**
     * method responsible for saving parameters
     *
     * @return
     */
    private String save(SubstantiveParametersDto substantiveParametersDto) {
        String id = substantiveParametersDto.getId();
        if (id == null) {
            substantiveParametersDto.setId(UUID.randomUUID().toString()); // w przypadku gdy parametr jeszcze nie istnieje - id jest równe null - należy go utworzyć
        }
        return substantiveParametersESRepository.save(substantiveParametersDto).getId();
    }

    @Override
    public List<SubstantiveParametersDto> listDto(SubstantiveParametersDtoQuery query) {
        return super.listDto(query);
    }

    @Override
    public Long listCount(SubstantiveParametersDtoQuery query) {
        return super.listCount(query);
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, SubstantiveParametersDtoQuery query) {
        if(query.getName() != null) {
            String parseValue = query.getName().replaceAll("[^a-zA-Z0-9ĄąĆćĘęŁłŃńÓóŚśŹźŻż]", " ");
            builder.must(buildSpanQuery("parameterNames.name", parseValue));
        }
        if(query.getKey() != null) {
            builder.must(buildSpanQuery("key", query.getKey()));
        }
        if(query.getTag() != null) {
            String parseValue = query.getTag().replaceAll("[^a-zA-Z0-9]", " ");
            builder.must(buildSpanQuery("tags", parseValue));
        }
        if (query.getCustomsOffice() != null) {
            String parseValue = query.getCustomsOffice().replaceAll("[^a-zA-Z0-9]", " ");
            builder.must(buildSpanQuery("customsOffice", parseValue));
        }
        if (query.getIsPerOffice() != null) {
            this.buildCustomsOfficeQuery(builder, query.getIsPerOffice());
        }

        return builder;
    }

    private void buildCustomsOfficeQuery(BoolQueryBuilder builder, Boolean isPerOffice) {
        TermQueryBuilder termQueryBuilder = new TermQueryBuilder("isPerOffice", isPerOffice);

        if(!isPerOffice) {
            BoolQueryBuilder notExistQueryBuilder = new BoolQueryBuilder();
            ExistsQueryBuilder existsQueryBuilder = new ExistsQueryBuilder("isPerOffice");
            notExistQueryBuilder.mustNot(existsQueryBuilder);

            BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
            boolQueryBuilder.should(termQueryBuilder);
            boolQueryBuilder.should(notExistQueryBuilder);

            builder.must(boolQueryBuilder);
        } else {
            builder.must(termQueryBuilder);
        }
    }

    private SpanNearQueryBuilder buildSpanQuery(String name, String value) {
        SpanNearQueryBuilder result = null;
        String[] parts = value.toLowerCase().split("\\s+");
        int i = 0;
        int last = parts.length-1;
        while(i<parts.length) {
            if(i==0||i==last) {
                String query = parts[i];
                if(i==0) {
                    query = "*"+query;
                }
                if(i==last) {
                    query = query + "*";
                }
                if(result==null) {
                    result = QueryBuilders.spanNearQuery(QueryBuilders.spanMultiTermQueryBuilder(QueryBuilders.wildcardQuery(name, query)),0);
                } else {
                    result = result.addClause(QueryBuilders.spanMultiTermQueryBuilder(QueryBuilders.wildcardQuery(name, query)));
                }
            } else {
                result = result.addClause(QueryBuilders.spanTermQuery(name, parts[i]));
            }

            i++;
        }
        return result;
    }

    @Override
    public SubstantiveParametersDto getDto(String key) {
        return substantiveParametersESRepository.findById(key).get();
    }

    @Override
    public String persistDto(SubstantiveParametersDto substantiveParametersDto) {
        return save(substantiveParametersDto);
    }

    @Override
    public void deleteDto(String key) {
        SubstantiveParametersDto substantiveParametersDto = substantiveParametersESRepository.findById(key).get();
        if (substantiveParametersDto != null) {
            substantiveParametersESRepository.delete(substantiveParametersDto);
        }
    }

    /**
     * Metoda umożliwiająca wyszukiwanie parametrów po kluczu.
     * W przypadku gdy parametr nie został znaleziony:
     * - definicja istnieje - nowy parametr zostanie utworzony
     * - definicja nie istnieje parametr nie może zostać utworzony
     *
     * @param key - klucz po którym parametr ma być znaleziony
     * @return Odnaleziony parametr w przypadku sukcesu - w przypadku niepowodzenia - null.
     */
    public SubstantiveParametersDto findSubstantiveParametersByKey(String key) {
        if (key == null) {
            return null;
        }

        SubstantiveParametersDto substantiveParametersDto = substantiveParametersESRepository.findByKey(key);

        if (substantiveParametersDto == null) {
            if (substantiveParametersParameterDefinition != null) {
                substantiveParametersDto = substantiveParametersParameterDefinition.createParameterFromDefinition(key);
            }
            if (substantiveParametersDto != null) {
                persistDto(substantiveParametersDto);
                //todo: not sure about the transaction here, needs to be clarified
                // persistTags(substantiveParametersDto.getTags());
            }

            return substantiveParametersDto;
        }

        if(substantiveParametersDto.isPerOffice()) {
            throw new RuntimeException("Substantive parameter need officeId!");
        }

        return substantiveParametersDto; // paramter pomyślnie odnaleziono
    }

    public SubstantiveParametersDto findSubstantiveParametersByKey(String key, String officeId) {
        if (key == null || officeId == null) {
            return null;
        }

        SubstantiveParametersDtoQuery dtoQuery = new SubstantiveParametersDtoQuery(key, officeId, true);
        List<SubstantiveParametersDto> parametersDtos = this.listDto(dtoQuery);

        if (parametersDtos.size() == 0) {
            SubstantiveParametersDto substantiveParametersDto = substantiveParametersParameterDefinition
                .createParameterFromDefinition(key, officeId);

            if (substantiveParametersDto != null) {
                persistDto(substantiveParametersDto);
            }

            return substantiveParametersDto;
        }

        return parametersDtos.get(0);
    }

    /**
     * Goes through the list of tags and adds new (not existing yet) to the Elastic Search
     * @param newTags
     */
    public void persistTags(List<String> newTags) {
        if (newTags == null || newTags.size() == 0) {
            return;
        }

        SubstantiveParametersTagDtoQuery query = new SubstantiveParametersTagDtoQuery();
        query.setSortProperty("name.keyword");
        List<String> tags = Optional.ofNullable(substantiveParametersTagDtoService.listDto(query)).map(item -> item.stream().map(SubstantiveParametersTagDto::getName)).orElseGet(Stream::empty).collect(Collectors.toList());

        tags.stream().map(tag -> {
            if (!tags.contains(tag)) {
                SubstantiveParametersTagDto substantiveParametersTagDto = new SubstantiveParametersTagDto();
                substantiveParametersTagDto.setName(tag);
                substantiveParametersTagDtoService.persistDto(substantiveParametersTagDto);
            }
            return null;
        });
    }
}
