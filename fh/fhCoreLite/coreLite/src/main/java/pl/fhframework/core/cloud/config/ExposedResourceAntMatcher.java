package pl.fhframework.core.cloud.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.FileSystems;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;

/**
 * Exposed resource ant matcher.
 */
@EqualsAndHashCode
public class ExposedResourceAntMatcher {

    @Getter
    @Setter
    private String antMatcher;

    @JsonIgnore
    private transient PathMatcher matcherImpl;

    public ExposedResourceAntMatcher() {
    }

    public ExposedResourceAntMatcher(String antMatcher) {
        this.antMatcher = antMatcher;
    }

    public boolean uriMatches(String uri) {
        return uri != null && getOrCreatePattern().matches(Paths.get(uri));
    }

    private PathMatcher getOrCreatePattern() {
        if (matcherImpl == null) {
            matcherImpl = FileSystems.getDefault().getPathMatcher("glob:" + antMatcher);
        }
        return matcherImpl;
    }
}
