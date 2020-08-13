package pl.fhframework;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpHeaders;

import java.io.Serializable;

/**
 * Created by Piotr on 2017-01-09.
 */
@Getter
@Setter
public class UserSessionDescription extends SessionDescription {

    private String userAddress;

    private String clientInfo;

    private HttpHeaders handshakeHeaders;

}
