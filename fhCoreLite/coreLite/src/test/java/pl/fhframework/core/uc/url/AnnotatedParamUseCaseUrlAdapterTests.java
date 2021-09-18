package pl.fhframework.core.uc.url;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.format.number.NumberStyleFormatter;
import pl.fhframework.core.uc.url.annotatedClasses.*;
import pl.fhframework.format.FhConversionService;

/**
 * Tests of use case URL parser
 */
public class AnnotatedParamUseCaseUrlAdapterTests {

    private static final String ALIAS = "alias";

    private static final ClassLoader LOADER = AnnotatedParamUseCaseUrlAdapterTests.class.getClassLoader();


    private AnnotatedParamsUseCaseUrlAdapter adapter;

    @Before
    public void init() {
        adapter = new AnnotatedParamsUseCaseUrlAdapter();
        adapter.conversionService = new FhConversionService();
        adapter.conversionService.addFormatterForFieldType(Integer.class, new NumberStyleFormatter());
    }

    @Test
    public void testExposingAndPassing() {
        // no param
        assertExposingAndStarting(new ExampleInitialUseCase(),
                createUrl(ALIAS, null, null, null, null, null, null));

        // one param
        assertExposingAndStarting(new StringInputUseCaseNoAnnotation(),
                createUrl(ALIAS, "value", null, null, null, null, null),
                "value");
        assertExposingAndStarting(new StringInputUseCasePositional(),
                createUrl(ALIAS, "value", null, null, null, null, null),
                "value");
        assertExposingAndStarting(new StringInputUseCaseQuery(),
                createUrl(ALIAS, null, null, StringInputUseCaseQuery.PARAM, "value", null, null),
                "value");

        // two params
        assertExposingAndStarting(new TwoStringInputUseCaseNoAnnotation(),
                createUrl(ALIAS, "value1", "value2", null, null, null, null),
                "value1", "value2");
        assertExposingAndStarting(new TwoStringInputUseCaseRevertedPositional(),
                createUrl(ALIAS, "value2", "value1", null, null, null, null),
                "value1", "value2");
        assertExposingAndStarting(new TwoIntInputUseCaseQuery(),
                createUrl(ALIAS, null, null, TwoIntInputUseCaseQuery.NAME1, "22", TwoIntInputUseCaseQuery.NAME2, "66"),
                22, 66);
        assertExposingAndStarting(new TwoStringInputUseCaseMixed(),
                createUrl(ALIAS, "value1", null, TwoStringInputUseCaseMixed.NAME, "value2", null, null),
                "value1", "value2");

        // entity
        assertExposingAndStarting(new TwoEntityInputUseCaseMixed(),
                createUrl(ALIAS, "22", null, TwoEntityInputUseCaseMixed.NAME, "66", null, null),
                new FakeEntity(22L), new FakeEntity(66L));

        // wrapper
        assertExposingAndStarting(new StringAndWrapperInputUseCaseNoAnnotation(),
                createUrl(ALIAS, "Edit", null, ParamWrapperNoAnnotation.NAME1, "John", ParamWrapperNoAnnotation.NAME2, "Black"),
                "Edit", new ParamWrapperNoAnnotation("John", "Black"));
//        assertExposingAndStarting(new TwoWrapperInputUseCaseNamedWithPrefix(),
//                createUrl(ALIAS, "Edit", null, ParamWrapperNamed.NAME1, "John", ParamWrapperNamed.NAME1, "Black"),
//                new ParamWrapperNamed("John", null), new ParamWrapperNamed("John", null));
    }

    private void assertExposingAndStarting(ParamStorageUseCase usecase, UseCaseUrl expectedUrl, Object... useCaseParams) {
        UseCaseUrl useCaseUrl = new UseCaseUrl();
        useCaseUrl.setUseCaseAlias(ALIAS);
        boolean ok = adapter.exposeURL(usecase, useCaseUrl, useCaseParams);
        Assert.assertTrue(ok);
        Assert.assertEquals(expectedUrl, useCaseUrl);

        ok = adapter.startFromURL(usecase, useCaseUrl);

        Assert.assertTrue(ok);
        Assert.assertArrayEquals(useCaseParams, usecase.getPassedParams());
    }

    private UseCaseUrl createUrl(String useCaseAlias,
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
