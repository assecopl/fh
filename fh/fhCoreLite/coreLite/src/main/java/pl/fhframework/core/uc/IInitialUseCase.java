package pl.fhframework.core.uc;

/**
 * Represents use case whose contract does not specify inputs and outputs.
 * It is capable of being started without any previous context (i.e. from user menu).
 */
public interface IInitialUseCase extends IUseCaseNoInput<IUseCaseNoCallback> {
}