package pl.fhframework.dp.commons.fh.uc.header;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import pl.fhframework.dp.commons.fh.declaration.handling.BaseDeclParams;
import pl.fhframework.dp.commons.fh.declaration.handling.BaseDeclarationHandlingUC;
import pl.fhframework.dp.commons.fh.declaration.handling.IDeclarationHandler;
import pl.fhframework.dp.commons.fh.uc.FhdpBaseUC;
import pl.fhframework.dp.commons.fh.uc.IGenericListOutputCallback;
import pl.fhframework.dp.transport.dto.commons.OperationResultBaseDto;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;

@UseCase
@Getter
@Setter
public abstract class FileUploaderBaseUC<PARAMS extends BaseDeclParams> extends FhdpBaseUC implements IInitialUseCase {

    @Autowired
    public ApplicationContext context;

    public PARAMS params;
    public String fileName;

    public class Callback implements IGenericListOutputCallback<OperationResultBaseDto> {
        @Override
        public void delete() {
        }

        @Override
        public void cancel() {
        }

        @Override
        public void save(OperationResultBaseDto operationResultBaseDto) {
            String declarationType = operationResultBaseDto.getOperationID();
            if(declarationType != null) {
                IDeclarationHandler declarationHandler = context.getBean(BaseDeclarationHandlingUC.HANDLER_BEAN_PREFIX + declarationType, IDeclarationHandler.class);
                String useCase = declarationHandler.getListLoaderClassName();
                if(useCase != null) {
                    getUserSession().runUseCase(useCase);
                }
            }
        }
    }

}
