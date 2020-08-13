package pl.fhframework.core.uc.handlers;

import pl.fhframework.core.uc.IUseCase;

public interface IUseCaseListener {
    /**
     * Called before usecase is started (runUseCase, runSubUseCase, runInitialUseCase, runSystemUseCase)
     *
     * @param useCase instance of UseCase
     * @param params  input params (in case od ICustomUseCase only registered inputs {@link pl.fhframework.core.uc.ICustomUseCase#registerInputs(Object...)})
     */
    void beforeRun(IUseCase useCase, Object[] params);

    /**
     * Called after usecase is started (runUseCase, runSubUseCase, runInitialUseCase, runSystemUseCase)
     *
     * @param useCase instance of UseCase
     * @param params  input params (in case od ICustomUseCase only registered inputs {@link pl.fhframework.core.uc.ICustomUseCase#registerInputs(Object...)})
     */
    void afterRun(IUseCase useCase, Object[] params);

    /**
     * Called before usecase is ended or terminated
     *
     * @param useCase instance of UseCase
     * @param params output params (only when usecase is ended through exit not forced terminated)
     */
    void beforeExit(IUseCase useCase, Object[] params);

    /**
     * Called after usecase is ended or terminated
     *
     * @param useCase instance of UseCase
     * @param params output params (only when usecase is ended through exit not forced terminated)
     */
    void afterExit(IUseCase useCase, Object[] params);
}
