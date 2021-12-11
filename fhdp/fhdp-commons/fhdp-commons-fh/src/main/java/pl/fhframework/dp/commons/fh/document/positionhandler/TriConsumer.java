package pl.fhframework.dp.commons.fh.document.positionhandler;

import java.util.Objects;

//Custom implementation of TriConsumer as this Functional interface is not available from std. java libraries
@FunctionalInterface
public interface TriConsumer<A,B,C> {

    void accept(A a, B b, C c);

    default TriConsumer<A, B, C> andThen(TriConsumer<? super A, ? super B, ? super C> after) {
        Objects.requireNonNull(after);

        return (l, r, n) -> {
            accept(l, r, n);
            after.accept(l, r, n);
        };
    }
}