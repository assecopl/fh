package pl.fhframework.dp.commons.services.changelog;

import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.els.repositories.ChangeLogESRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.transport.changelog.ChangeLogDto;
import pl.fhframework.dp.transport.changelog.ChangeLogDtoQuery;
import pl.fhframework.dp.transport.service.IChangeLogDtoService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 */
@Service
@Slf4j
public class ChangeLogDtoService extends GenericDtoService<String, ChangeLogDto, ChangeLogDto, ChangeLogDtoQuery, ChangeLogDto> implements IChangeLogDtoService {

    @Autowired
    ChangeLogESRepository changeLogESRepository;

    public ChangeLogDtoService() {
        super(ChangeLogDto.class, ChangeLogDto.class, ChangeLogDto.class);
    }

    @Override
    public List<ChangeLogDto> listDto(ChangeLogDtoQuery query) {
        if(query.getSortProperty() == null || "id".equals(query.getSortProperty())) {
            query.setSortProperty("changeTime");
            query.setAscending(false);
        }
        return super.listDto(query);
    }

    @Override
    public String persistDto(ChangeLogDto changeLogDto) {
        return changeLogESRepository.save(changeLogDto).getId();
    }

    @Override
    protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, ChangeLogDtoQuery query) {
        if(query.getId() != null) {
            builder.must(QueryBuilders.termsQuery("id.keyword", query.getId()));
        }
         if(query.getDescription() != null) {
            builder.must(QueryBuilders.wildcardQuery("description", query.getDescription() + "*"));
        }

        LocalDateTime dateFrom = query.getTimeFrom();
        LocalDateTime dateTo = query.getTimeTo();
        if (dateFrom != null || dateTo != null) {
            builder.filter(QueryBuilders.rangeQuery("changeTime")
                    .from(dateFrom)
                    .to(dateTo)
                    .includeLower(dateFrom != null)
                    .includeUpper(dateTo != null));
        }
        return builder;
    }

    public String getLastChangePerformed() {
        ChangeLogDtoQuery query = new ChangeLogDtoQuery();
        query.setSize(1);
        List<ChangeLogDto> list = listDto(query);
        if(list.isEmpty()) {
            return null;
        } else {
            return list.get(0).getId();
        }
    }

    public void registerChange( String id, String description) {
        ChangeLogDto dto = new ChangeLogDto(id, description);
        persistDto(dto);
    }


}
