package pl.fhframework.dp.commons.fh.importers.file;

import org.springframework.core.io.Resource;
import pl.fhframework.dp.commons.fh.declaration.handling.BaseDeclParams;
import pl.fhframework.dp.commons.fh.uc.IGenericListOutputCallback;
import pl.fhframework.dp.commons.fh.uc.header.FileUploaderBaseUC;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;

import javax.xml.bind.ValidationException;

public interface IBaseMessageFileImporter<PARAMS extends BaseDeclParams> {

    void pullFileMessage(Resource input, FileUploaderBaseUC useCase) throws ValidationException;

    void runDeclUseCase(PARAMS params, IGenericListOutputCallback<OperationResultBaseDto> callback);
}
