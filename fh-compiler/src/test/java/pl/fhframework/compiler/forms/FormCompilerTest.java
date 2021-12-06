package pl.fhframework.compiler.forms;

import junit.framework.Assert;
import lombok.Getter;
import lombok.Setter;
import org.junit.Test;
import org.springframework.data.domain.Page;

import pl.fhframework.annotations.AvailabilityRuleMethod;

import pl.fhframework.compiler.core.dynamic.DynamicClassArea;
import pl.fhframework.compiler.core.dynamic.dependency.DependenciesContext;
import pl.fhframework.compiler.core.dynamic.dependency.DependencyTimestampJavaGenerator;
import pl.fhframework.compiler.core.generator.AbstractJavaCodeGenerator;
import pl.fhframework.compiler.core.generator.DynamicClassCompiler;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.io.FhResource;
import pl.fhframework.helper.AutowireHelper;
import pl.fhframework.model.forms.*;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;

/**
 * Created by Piotr on 2017-02-01.
 */
// TODO: CORE_LITE_MOVE move this test to new module for advanced controls
public class FormCompilerTest {

    private static final String XML_FILE = "TestForm.frm";

    public interface ITest {

    }

    @Getter
    @Setter
    public static class TestModel {

        private ITest itest;

        private String one;

        private String two;

        private int counter;

        private Person man;

        private List<Person> peopleList = new ArrayList<>(Arrays.asList(new Person(), new Person()));

        private Set<Person> peopleSet = new HashSet<>(Arrays.asList(new Person(), new Person()));

        private Person[] peopleArray = { new Person(), new Person() };

        private Page<Person> peoplePage;

        private PageModel<Person> peoplePageModel;

        private CollectionPageableModel<Person> peopleCollectionPageModel;

        public Person extractManFromModel(TestModel model) {
            return model.getMan();
        }

    }

    @Getter
    @Setter
    public static class Person {
        private long id = 123L;
        private String firstName = "Jack";
        private String lastName = "Black";
        private Person parent;
        private boolean happy;
        private Set<Person> childrenSet = new HashSet<>(Arrays.asList(new Person(), new Person()));

        public String sayHello(Person otherPerson) {
            return "Hello " + otherPerson.getFirstName() + "!";
        }

        public static String sayHelloStatic(Person otherPerson) {
            return "Hello " + otherPerson.getFirstName() + "!";
        }

        public String sayHello(Person otherPerson1, Person otherPerson2) {
            return "Hello " + otherPerson1.getFirstName() + " and " + otherPerson2.getFirstName() + "!";
        }
    }

    public static class TestForm extends Form<TestModel> {

        @AvailabilityRuleMethod("listAccess1")
        @AvailabilityRuleMethod("listAccess2")
        protected AccessibilityEnum myMethod(AccessibilityRule rule) {
            return AccessibilityEnum.DEFECTED;
        }

        public String formatPerson(Person person) {
            return person.getFirstName();
        }

        public boolean isPersonHappy(Object person) {
            return Person.class.cast(person).isHappy();
        }
    }

    @Test
    public void testCompilerExample() throws IOException {
        AutowireHelper.disable();
        String formFilePath = new File(URLDecoder.decode(FormCompilerTest.class.getResource("/"+XML_FILE).getFile(), "UTF-8")).getAbsolutePath();

        String baseClassName = XML_FILE.replace(".frm", "");
        String targetClassName = baseClassName + "_v1";
        String targetPackage = FormCompilerTest.class.getPackage().getName();

        FormsManager formsManager = new FormsManager();

        TestForm xmlInstance = new TestForm();
        formsManager.buildFromFile(FhResource.get(Paths.get(formFilePath)), xmlInstance);

        // just to test generation of timestamps method
        Map<DynamicClassName, Instant> timestamps = new HashMap<>();
        timestamps.put(DynamicClassName.forStaticBaseClass(FormCompilerTest.class), Instant.now());

        FormsJavaCodeGenerator generator = new FormsJavaCodeGenerator(
                xmlInstance, TestModel.class, targetPackage, targetClassName, baseClassName, new DependenciesContext(),
                DependencyTimestampJavaGenerator.generateStaticJavaMethod(timestamps), Optional.empty());
        String code = generator.generateClass();

        Path target = Paths.get(formFilePath).getParent().getParent().getParent().resolve("target/test-output");
        if (!Files.exists(target)) {
            Files.createDirectory(target);
        }

        DynamicClassCompiler compiler = new DynamicClassCompiler(target.toAbsolutePath().toString());
        Path file = compiler.createDynamicJavaFile(code, targetPackage, targetClassName);
        compiler.compile(Arrays.asList(file), DynamicClassArea.FORM.isAspectWeavingNeeded());
        compiler.loadDynamicClass(targetPackage, targetClassName);
    }

    private FormCompilerTest[] array;
    private FormCompilerTest.TestModel[] innerClassArray;
    private Map<?,FormCompilerTest> wildcardMap;

    @Test
    public void testToTypeLiteral() throws Exception {
        Assert.assertEquals("pl.fhframework.compiler.forms.FormCompilerTest[]", AbstractJavaCodeGenerator.toTypeLiteral(getClass().getDeclaredField("array").getGenericType()));
        Assert.assertEquals("pl.fhframework.compiler.forms.FormCompilerTest.TestModel[]", AbstractJavaCodeGenerator.toTypeLiteral(getClass().getDeclaredField("innerClassArray").getGenericType()));
        Assert.assertEquals("java.util.Map<?, pl.fhframework.compiler.forms.FormCompilerTest>", AbstractJavaCodeGenerator.toTypeLiteral(getClass().getDeclaredField("wildcardMap").getGenericType()));
    }
}
