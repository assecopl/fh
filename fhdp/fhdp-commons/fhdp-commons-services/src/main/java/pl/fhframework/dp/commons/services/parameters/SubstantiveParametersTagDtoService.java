package pl.fhframework.dp.commons.services.parameters;

import org.elasticsearch.index.query.BoolQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.fhframework.dp.commons.model.repositories.SubstantiveParametersTagESRepository;
import pl.fhframework.dp.commons.services.facade.GenericDtoService;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersTagDtoQuery;
import pl.fhframework.dp.transport.service.ISubstantiveParametersTagDtoService;

import java.util.List;
import java.util.UUID;

@Service("substantiveParametersTagDtoService")
public class SubstantiveParametersTagDtoService
    extends GenericDtoService<String, SubstantiveParametersTagDto, SubstantiveParametersTagDto, SubstantiveParametersTagDtoQuery, SubstantiveParametersTagDto>
    implements ISubstantiveParametersTagDtoService {

  @Autowired
  SubstantiveParametersTagESRepository substantiveParametersTagESRepository;

  public SubstantiveParametersTagDtoService() {
    super(SubstantiveParametersTagDto.class, SubstantiveParametersTagDto.class, SubstantiveParametersTagDto.class);
  }

  private String save(SubstantiveParametersTagDto substantiveParametersTagDto) {
    String id = substantiveParametersTagDto.getId();
    if (id == null) {
      substantiveParametersTagDto.setId(UUID.randomUUID().toString());
    }
    return substantiveParametersTagESRepository.save(substantiveParametersTagDto).getId();
  }

  @Override
  public List<SubstantiveParametersTagDto> listDto(SubstantiveParametersTagDtoQuery query) {
    return super.listDto(query);
  }

  @Override
  protected BoolQueryBuilder extendQueryBuilder(BoolQueryBuilder builder, SubstantiveParametersTagDtoQuery query) {
    return builder;
  }

  @Override
  public String persistDto(SubstantiveParametersTagDto substantiveParametersTagDto) {
    return save(substantiveParametersTagDto);
  }

  @Override
  public SubstantiveParametersTagDto getDto(String key) {
    return substantiveParametersTagESRepository.findById(key).orElse(null);
  }

  @Override
  public void deleteDto(String key) {
    SubstantiveParametersTagDto entity = getDto(key);
    if(entity != null) {
      substantiveParametersTagESRepository.delete(entity);
    }
  }
}

