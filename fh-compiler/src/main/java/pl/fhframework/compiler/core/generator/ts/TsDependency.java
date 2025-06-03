package pl.fhframework.compiler.core.generator.ts;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import pl.fhframework.core.util.StringUtils;

@Getter
@Setter
@Builder
@EqualsAndHashCode
public class TsDependency {
    private String name;

    private String appName;
    private String module;

    private String filePath;

    public String getFrom() {
        if (!StringUtils.isNullOrEmpty(getFilePath())) {
            return getFilePath();
        } else {
            return String.format("@%s/%s", getAppName(), getModule());
        }
    }
}
