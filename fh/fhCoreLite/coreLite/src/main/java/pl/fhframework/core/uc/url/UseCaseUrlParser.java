package pl.fhframework.core.uc.url;

import pl.fhframework.core.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Optional;

/**
 * Use case URL parser
 */
public class UseCaseUrlParser {

    public Optional<UseCaseUrl> parseUrl(String url) {
        if (url == null) {
            return Optional.empty();
        }

        String[] hashSplitted = url.split("#[/]?", 2);
        if (hashSplitted.length < 2) {
            return Optional.empty();
        }

        String[] paramsQuestionSplitted = hashSplitted[1].split("\\?", 2);

        String[] positionalParameters = paramsQuestionSplitted[0].split("/");
        if (positionalParameters.length < 1) {
            return Optional.empty();
        }

        UseCaseUrl parsedUrl = new UseCaseUrl();
        parsedUrl.setUrl(url);
        // first positional parameter is an UC alias
        parsedUrl.setUseCaseAlias(decodeParam(positionalParameters[0]));
        positionalParameters = Arrays.copyOfRange(positionalParameters, 1, positionalParameters.length);
        for (int i = 0; i < positionalParameters.length; i++) {
            parsedUrl.putPositionalParameter(i, decodeParam(positionalParameters[i]));
        }

        parseUrlParamsQuestion(parsedUrl, paramsQuestionSplitted);

        return Optional.of(parsedUrl);
    }

    public UseCaseUrl parseUrlQuestionParams(String url) {
        UseCaseUrl parsedUrl = new UseCaseUrl();
        parsedUrl.setUrl(url);

        String[] paramsQuestionSplitted = url.split("\\?", 2);

        parseUrlParamsQuestion(parsedUrl, paramsQuestionSplitted);

        return parsedUrl;
    }

    private void parseUrlParamsQuestion(UseCaseUrl parsedUrl, String[] paramsQuestionSplitted) {
        if (paramsQuestionSplitted.length == 2) {
            String[] namedParameters = paramsQuestionSplitted[1].split("&");
            for (int i = 0; i < namedParameters.length; i++) {
                String[] namedParamParts = namedParameters[i].split("=", 2);
                String namedParamName = namedParamParts[0];
                String namedParamValue = namedParamParts.length == 2 ? namedParamParts[1] : "";
                parsedUrl.putNamedParameter(namedParamName, decodeParam(namedParamValue));
            }
        }
    }

    public String formatUrl(UseCaseUrl url) {
        StringBuilder output = new StringBuilder();
        output.append('#').append(url.getUseCaseAlias()).append('/');

        // positional parameters
        for (int i = 0; i <= url.getMaxPositionalIndex(); i++) {
            output.append(encodeParam(url.getPositionalParameter(i))).append('/');
        }
        // strip all / at the end
        while (output.length() > 0 && output.charAt(output.length() - 1) == '/') {
            output.delete(output.length() - 1, output.length());
        }

        // named parameters
        if (!url.getParameterNames().isEmpty()) {
            output.append('?');
            boolean first = true;
            for (String name : url.getParameterNames()) {
                String value = url.getNamedParameter(name);
                if (!first) {
                    output.append('&');
                }
                output.append(name).append('=').append(encodeParam(value));
                first = false;
            }
        }

        return output.toString();
    }

    private String decodeParam(String param) {
        if (!StringUtils.isNullOrEmpty(param)) {
            try {
                return URLDecoder.decode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return null;
        }
    }


    private String encodeParam(String param) {
        if (!StringUtils.isNullOrEmpty(param)) {
            try {
                return URLEncoder.encode(param, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        } else {
            return "";
        }
    }
}
