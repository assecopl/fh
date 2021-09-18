package pl.fhframework.core.uc;

import java.util.function.Consumer;

/**
 * Represents use case callback with two possible distinct return values
 * @param <T> First parameter value type
 * @param <V> Second parameter value type
 */
public interface IUseCaseTwoOutputCallback<T, V> extends IUseCaseOutputCallback {
    /**
     * Outputs the first possible value to the parent use case
     * @param one Value to be outputted to parent use case
     */
    void output1(T one);
    /**
     * Outputs the second possible value to the parent use case
     * @param two Value to be outputted to parent use case
     */
    void output2(V two);

    /**
     * Creates use case callback from provided {@link Consumer} instance
     * @param outputMethod1 Instance of method implementing {@link Consumer} for the first return value
     * @param outputMethod2 Instance of method implementing {@link Consumer} for the second return value
     * @param <T> First output value type
     * @param <V> Second output value type
     * @return Callback instance from provided parameters
     */
    static <T, V> IUseCaseTwoOutputCallback<T, V> getCallback(Consumer<T> outputMethod1, Consumer<V> outputMethod2) {
        return new IUseCaseTwoOutputCallback<T,V>() {
            @Override
            public void output1(T one) {
                outputMethod1.accept(one);
            }

            @Override
            public void output2(V two) {
                outputMethod2.accept(two);
            }
        };
    }
}
