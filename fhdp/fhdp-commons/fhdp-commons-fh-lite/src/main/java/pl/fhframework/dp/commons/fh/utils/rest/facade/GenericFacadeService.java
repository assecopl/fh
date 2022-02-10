package pl.fhframework.dp.commons.fh.utils.rest.facade;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.dto.commons.BaseDtoQuery;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;
import pl.fhframework.dp.transport.service.IDtoService;
import pl.fhframework.model.forms.PageModel;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 26.08.2019
 */
public class GenericFacadeService<ID, DTO extends IPersistentObject, LIST extends IPersistentObject, QUERY extends BaseDtoQuery> implements IDtoService<ID, DTO , LIST , QUERY> {
    @Autowired
    protected FacadeClientFactory clientFactory;
    private Class serviceClazz;

    public GenericFacadeService(Class serviceClazz) {
        this.serviceClazz = serviceClazz;
    }

    @Override
    public NameValueDto getCode(String refDataCode, String code, LocalDate onDate, Map params) {
        IDtoService service = clientFactory.createServiceProxy(serviceClazz);
        return service.getCode(refDataCode, code, onDate, params);
    }

    @Override
    public List<NameValueDto> listCodeList(String code, String text, LocalDate onDate, Map params) {
        IDtoService service = clientFactory.createServiceProxy(serviceClazz);
        return service.listCodeList(code, text, onDate, params);
    }

    public List<NameValueDto> listCodeListPageable(Pageable pageable, String code, String text, LocalDate onDate, Map params) {
        IDtoService service = clientFactory.createServiceProxy(serviceClazz);
        params.put("page", String.valueOf(pageable.getPageNumber()));
        params.put("size", String.valueOf(pageable.getPageSize()));
        return service.listCodeList(code, text, onDate, params);
    }

    public PageModel<NameValueDto> listCodeListPaged(String code, String text, LocalDate onDate, Map params) {
        long total = countCodeList(code, text, onDate, params);
        return new PageModel<NameValueDto>(pageable -> loadCodeListPage(pageable, code, text, onDate, params, total));

    }

    private Page<NameValueDto> loadCodeListPage(Pageable pageable, String code, String text, LocalDate onDate, Map params, long total) {
        IDtoService restService = clientFactory.createServiceProxy(serviceClazz);

        Page<NameValueDto> ret = new PageImpl<NameValueDto>(listCodeListPageable(pageable, code, text, onDate, params), pageable, total);
        return ret;
    }



    public Long countCodeList(String code, String text, LocalDate onDate, Map params) {
        IDtoService service = clientFactory.createServiceProxy(serviceClazz);
        return service.countCodeList(code, text, onDate, params);
    }

    @Override
    public List<LIST> listDto(QUERY query) {
        if(query == null) {
            throw new RuntimeException("query can not be null");
        }
        IDtoService restService = clientFactory.createServiceProxy(serviceClazz);
        return restService.listDto(query);
    }

    public PageModel<LIST> listDtoPaged(QUERY query) {
        if(query == null) {
            throw new RuntimeException("query can not be null");
        }
        long total = listCount(query);
        query.setFirstRow(0);
        query.setSize(10);
        return new PageModel<LIST>(pageable -> loadRegisterHFPage(pageable, query, total));
    }

    public List<LIST> listDtoPageable(Pageable pageable, QUERY query) {
        if(query == null) {
            throw new RuntimeException("query can not be null");
        }
        updateQuery(query, pageable);
        IDtoService restService = clientFactory.createServiceProxy(serviceClazz);
        return restService.listDto(query);
    }

    public Page<LIST> loadRegisterHFPage(Pageable pageable, QUERY query, long total) {
        IDtoService restService = clientFactory.createServiceProxy(serviceClazz);
        updateQuery(query, pageable);
        return new PageImpl<LIST>(listDtoPageable(pageable, query), pageable, total);
    }

    private void updateQuery(QUERY query, Pageable pageable) {
        query.setFirstRow(Math.toIntExact(pageable.getOffset()));
        query.setSize(pageable.getPageSize());
        updateSortOrder(query, pageable.getSort());
    }

    public void updateSortOrder(BaseDtoQuery query, Sort sort) {
        String sortProperty = query.getSortProperty();
        Boolean ascending = query.getAscending();
        if (sort != null && sort.isSorted()) {
            Optional<Sort.Order> sOpt = sort.stream().findFirst();
            if (sOpt.isPresent()) {
                Sort.Order so = sOpt.get();
                sortProperty = so.getProperty();
                ascending = so.getDirection() != null && so.getDirection().isAscending();
            }
        }
        query.setAscending(ascending);
        query.setSortProperty(sortProperty);
    }

    @Override
    public Long listCount(QUERY query) {
         return clientFactory.createServiceProxy(serviceClazz).listCount(query);
    }

    @Override
    public DTO getDto(ID key) {
        return (DTO) clientFactory.createServiceProxy(serviceClazz).getDto(key);
    }

    @Override
    public ID persistDto(DTO dto) {
        return (ID) clientFactory.createServiceProxy(serviceClazz).persistDto(dto);
    }

    @Override
    public void deleteDto(ID key) {
        clientFactory.createServiceProxy(serviceClazz).deleteDto(key);
    }
}
