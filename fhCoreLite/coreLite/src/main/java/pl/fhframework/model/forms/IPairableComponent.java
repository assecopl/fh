package pl.fhframework.model.forms;

public interface IPairableComponent<T> {
    /**
     * Element łączyący w pary komponenty
     */
    T getPairDiscriminator();

    /**
     * Ustawienie danych zgrupowanym komponentom
     */
    void setPairData(T data);
}
