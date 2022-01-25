/*
 * SemaforStatusEnum.java
 * 
 * Prawa autorskie do oprogramowania i jego kodów źródłowych 
 * przysługują w pełnym zakresie wyłącznie SKG S.A.
 * 
 * All copyrights to software and its source code
 * belong fully and exclusively to SKG S.A.
 */
package pl.fhframework.dp.commons.base.semafor;

import java.io.Serializable;

/**
 * User: dariuszs
 * Date: 16.11.11
 * Time: 14:06
 *
 * @version $Revision: 1366 $, $Date: 2018-10-08 09:21:11 +0200 (Mon, 08 Oct 2018) $
 */
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
