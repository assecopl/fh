package pl.fhframework.core.logging;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Error information send from server to client.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(value = {"exception"})
public class ErrorInformation implements Serializable {

    /**
     * Marker interface of an error type enum. Implemented by an implementation specific enum.
     * ErrorType enum is used inside fh.
     */
    public interface IErrorType {
    }

    public enum ErrorType implements IErrorType {

        /**
         * Unsepcified server error
         */
        UNSPECIFIED_ERROR,

        /**
         * External data change detected with optimistic locking
         */
        EXTERNAL_DATA_CHANGE
    }

    private String id;

    private Enum<? extends IErrorType> errorType;

    private LocalDateTime timestamp;

    private String nodeName;

    private String message;

    @JsonProperty("exception")
    private Throwable exception;
}
