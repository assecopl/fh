package pl.fhframework.dp.transport.service;


import pl.fhframework.dp.commons.base.model.IPersistentObject;
import pl.fhframework.dp.transport.dto.commons.NameValueDto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Interfejs do automatycznej obsługi obiektów DTO na fasadzie
 *
 * @param <ID>    - klasa id encji
 * @param <DTO>   - klasa Dto dla encji
 * @param <LIST>  - klasa Dto dla listy
 * @param <QUERY> - klasa Query
 */
public interface IDtoService<ID, DTO extends IPersistentObject, LIST extends IPersistentObject, QUERY> {
    /**
     * Odczyt listy
     *
     * @param query - obiekt query

     * @return - lista obiektów Dto dla listy
     */
    default List<LIST> listDto(QUERY query) {
        throw new UnsupportedOperationException();
    }

    /**
     * Pobranie ilości elementów dla listy
     *
     * @param query - obiekt query
     * @return - ilość obiektów spełniających kryteria
     */
    default Long listCount(QUERY query) {
        throw new UnsupportedOperationException();
    }

    /**
     * Odczyt obiektu Dto
     *
     * @param key - klucz
     * @return - gdy odczytano obiekt dla wybranego klucza to obiekt Dto, w przeciwnym wypadku null
     */
    default DTO getDto(ID key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Zapis obiektu Dto do bazy
     *
     * @param dto - obiekt do zapisu
     * @return - klucz zapisanaego obiektu
     */
    default ID persistDto(DTO dto) {
        throw new UnsupportedOperationException();
    }

    /**
     * Usunięcie wybranego, po kluczu obiektu
     *
     * @param key - klucz obiektu do usunięcia
     */
    default void deleteDto(ID key) {
        throw new UnsupportedOperationException();
    }

    /**
     * Method returns requested code list
     * @param code: code list id
     * @param text: search text
     * @param onDate: reference date
     * @param params: optional additional params
     * @return List of name-value pairs.
     */
    default List<NameValueDto> listCodeList(String code, String text, LocalDate onDate, Map params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Method returns requested code list
     * @param code: code list id
     * @param text: search text
     * @param onDate: reference date
     * @param params: optional additional params
     * @return List of name-value pairs.
     */
    default Long countCodeList(String code, String text, LocalDate onDate, Map params) {
        throw new UnsupportedOperationException();
    }

    /**
     * Method return ref data code
     * @param refDataCode: code list id
     * @param code: code value
     * @param onDate: reference date
     * @param params: optional additional params
     * @return
     */
    default NameValueDto getCode(String refDataCode, String code, LocalDate onDate, Map params) {
        throw new UnsupportedOperationException();
    }
}
