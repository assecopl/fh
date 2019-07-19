package pl.fhframework.core.uc.meta;

import pl.fhframework.core.uc.IUseCase;
import pl.fhframework.subsystems.Subsystem;

import java.util.Optional;

public interface IUseCaseMetadataReader {

    Optional<UseCaseInfo> buildUseCaseMetadata(String useCaseClassName);

    UseCaseInfo buildUseCaseMetadata(Class<? extends IUseCase> useCaseClazz, Subsystem subsystem);
}