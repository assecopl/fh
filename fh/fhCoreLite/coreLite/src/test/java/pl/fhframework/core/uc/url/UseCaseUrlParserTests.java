package pl.fhframework.core.uc.url;

import org.junit.Assert;
import org.junit.Test;

/**
 * Tests of use case URL parser
 */
public class UseCaseUrlParserTests {

    private UseCaseUrlParser parser = new UseCaseUrlParser();

    @Test
    public void testParsing() {
        assertParsed("host:8080/#useCase/123/Manager", create("useCase", "123", "Manager", null, null, null, null));
        assertParsed("host:8080/#useCase/123/Manager?mode=EDIT", create("useCase", "123", "Manager", "mode", "EDIT", null, null));
        assertParsed("host:8080/#useCase/123?mode=EDIT", create("useCase", "123", null, "mode", "EDIT", null, null));
        assertParsed("host:8080/#useCase/123/Manager?mode=EDIT&mode2=TEST", create("useCase", "123", "Manager", "mode", "EDIT", "mode2", "TEST"));
        assertParsed("host:8080/#useCase/12+3/%C5%82?mode=p%26g&mode2=%3F%2F+%3D%5C", create("useCase", "12 3", "ł", "mode", "p&g", "mode2", "?/ =\\"));
    }

    @Test
    public void testFormatting() {
        assertFormatted(create("useCase", "123", "Manager", null, null, null, null), "#useCase/123/Manager");
        assertFormatted(create("useCase", "123", "Manager", "mode", "EDIT", null, null), "#useCase/123/Manager?mode=EDIT");
        assertFormatted(create("useCase", "123", null, "mode", "EDIT", null, null), "#useCase/123?mode=EDIT");
        assertFormatted(create("useCase", "123", "Manager", "mode", "EDIT", "mode2", "TEST"), "#useCase/123/Manager?mode=EDIT&mode2=TEST");
        assertFormatted(create("useCase", "12 3", "ł", "mode", "p&g", "mode2", "?/ =\\"), "#useCase/12+3/%C5%82?mode=p%26g&mode2=%3F%2F+%3D%5C");
    }

    private void assertFormatted(UseCaseUrl url, String expected) {
        Assert.assertEquals(expected, parser.formatUrl(url));
    }

    private void assertParsed(String url, UseCaseUrl expected) {
        Assert.assertEquals(expected, parser.parseUrl(url).get());
    }

    private UseCaseUrl create(String useCaseAlias,
                              String paramValue1, String paramValue2,
                              String paramNamedName1, String paramNamedValue1,
                              String paramNamedName2, String paramNamedValue2) {
        UseCaseUrl url = new UseCaseUrl();
        url.setUseCaseAlias(useCaseAlias);
        if (paramValue1 != null) {
            url.putPositionalParameter(0, paramValue1);
        }
        if (paramValue2 != null) {
            url.putPositionalParameter(1, paramValue2);
        }
        if (paramNamedName1 != null) {
            url.putNamedParameter(paramNamedName1, paramNamedValue1);
        }
        if (paramNamedName2 != null) {
            url.putNamedParameter(paramNamedName2, paramNamedValue2);
        }
        return url;
    }
}
