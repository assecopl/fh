package pl.fhframework.dp.commons.base.semafor;

import java.io.Serializable;

public enum SemaphoreStatusEnum implements Serializable {
    ValidNew,
    ValidProlonged,
    Invalid
    ;

    private static final long serialVersionUID = 1L;

    public static boolean isValid(SemaphoreStatusEnum semaphoreStatus) {
        return !Invalid.equals(semaphoreStatus);
    }
}
