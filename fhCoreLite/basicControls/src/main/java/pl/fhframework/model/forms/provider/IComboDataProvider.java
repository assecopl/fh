package pl.fhframework.model.forms.provider;

/**
 * @author Paweł Domański pawel.domanski@asseco.pl
 */
public interface IComboDataProvider<SRC, RESULT> {
//    // bez sygnatury getValues
//    default RESULT getValue(SRC element) {return (RESULT) element;}
//    public String getDisplayValue(SRC element);

    /**
     * Metoda zwraca listę obiektów słownika danego typu na podstawie tekstu wprowadzonego przez użytkownika.
     * @param text - tekst wprowadzony przez użytkownika

     * @return
     */
//    List<SRC> getValues(String text, Object... param);


    /**
     * Metoda zwraca element słownika o danym kodzie
     * @param code - wartość słownika przechowana w polu bindowanym z value

     * @return
     */
//    SRC getValue(RESULT code, Object... param);

    /**
     * Metoda zwracająca wartość elementu słownika
     * @param element
     * @return
     */
    RESULT getCode(SRC element);

    /**
     * Metoda zwracająca wyświetlaną zawartość słwnika z danego elementu
     * @param element
     * @return
     */
    String getDisplayValue(SRC element);


    /**
     * Metoda porównujica zwracane obiekty
     * @param element1
     * @param element2
     * @return
     */
    Boolean areObjectsEquals(RESULT element1, RESULT element2);

}
