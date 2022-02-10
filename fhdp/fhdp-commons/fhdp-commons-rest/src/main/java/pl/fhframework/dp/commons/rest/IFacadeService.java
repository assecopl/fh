package pl.fhframework.dp.commons.rest;


public interface IFacadeService {
    /**
     * Odczyt listy obiektów Dto
     *
     * @param request - wyamagane ustawienie obiektu query i klasy DTO
     * @return - lista obiektów DTO wg zadanych kryteriów
     */
    EntityRestResponse list(EntityRestRequest request);

    /**
     * Odczyt ilości elementów listy spełniających kryteria zapytania
     *
     * @param request - wyamagane ustawienie obiektu query i klasy DTO
     * @return - lista obiektów DTO wg zadanych kryteriów
     */
    EntityRestResponse count(EntityRestRequest request);

    /**
     * Odczyt obiektu Dto
     *
     * @param request - wymagane ustawienie identyfktora DTO i klasy obiektu DTO
     * @return - odczytany obiektu lub null gdy nie znaleziono takiego obiektu.
     */
    EntityRestResponse getDto(EntityRestRequest request);

    /**
     * @param request
     * @return
     */
    EntityRestResponse deleteDto(EntityRestRequest request);

    /**
     * @param request
     * @return
     */
    EntityRestResponse persistDto(EntityRestRequest request);

    /**
     * Obsługa wywołań operacji.
     *
     * @param request
     * @return
     */
    OperationRestResponse performOperation(OperationRestRequest request);

    OperationDataRestResponse getOperationData(OperationDataRestRequest request);

    OperationStateRestResponse getOperationState(OperationStateRestRequest request);

    EntityRestResponse listCodeList(CodelistRestRequest request);

    EntityRestResponse countCodeList(CodelistRestRequest request);

    EntityRestResponse getCode(CodelistRestRequest request);
}
