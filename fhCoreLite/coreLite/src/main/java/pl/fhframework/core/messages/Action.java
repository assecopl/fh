package pl.fhframework.core.messages;

/**
 *
 * Created by krzysztof.kobylarek on 2016-10-20.
 * An action without arguments [todo: can be replaced with runnable]
 */

@FunctionalInterface
public interface Action {//TODO:Maybe move to formsHandler package? Or maybe usecase should be in another module?

    void doAction();

}
