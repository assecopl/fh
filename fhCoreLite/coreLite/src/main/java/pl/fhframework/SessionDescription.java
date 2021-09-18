package pl.fhframework;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by Piotr on 2017-01-09.
 */
@Getter
@Setter
public class SessionDescription implements Serializable {
    private String serverAddress;

    private String conversationUniqueId;
}
