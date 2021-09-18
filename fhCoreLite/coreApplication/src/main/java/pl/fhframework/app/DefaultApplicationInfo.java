package pl.fhframework.app;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import pl.fhframework.core.IApplicationInformation;

/**
 * Created by krzysztof.kobylarek on 2017-05-22.
 */
@Component
@Getter
@Setter
public class DefaultApplicationInfo implements  IApplicationInformation{
    @Value("${build.branch}")
    private String branchName;

    @Value("${build.number}")
    private String buildNumber;

    @Value("${build.timestamp}")
    private String buildTimestamp;
}
