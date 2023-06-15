package pl.fhframework.fhdp.example.embedded;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.annotations.Action;
import pl.fhframework.core.logging.FhLogger;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;
import pl.fhframework.core.uc.url.UseCaseWithUrl;
import pl.fhframework.event.EventRegistry;

@UseCase
@UseCaseWithUrl(alias = "embedded")
public class EmbeddedViewUC implements IInitialUseCase {
//    @Autowired
//    private ExampleAppMessageHelper messageHelper;
    @Autowired
    private EventRegistry eventRegistry;

    private EmbeddedViewForm.Model model;

    @Override
    public void start() {
        EmbeddedViewForm.Model model = new EmbeddedViewForm.Model();
//        model.setSampleField(messageHelper.getMessage("sample.test" ));
        model.setExampleXML("<root>\n" +
                "  <dish larger=\"ruler\">-810789037.2195714</dish>\n" +
                "  <up native=\"people\">\n" +
                "    <mile shoe=\"package\">1911600795</mile>\n" +
                "    <characteristic needs=\"feet\">memory</characteristic>\n" +
                "    <grade common=\"highway\">-2131648163.785833</grade>\n" +
                "    <yesterday>\n" +
                "      <circle>\n" +
                "        <busy shake=\"saved\">-1637049036.5980804</busy>\n" +
                "        <roar consist=\"paint\">270193370.6921158</roar>\n" +
                "        <hard>391405598</hard>\n" +
                "        <been low=\"aside\">mysterious</been>\n" +
                "        <house loose=\"bad\">pipe</house>\n" +
                "        <write>entire</write>\n" +
                "      </circle>\n" +
                "      <price private=\"spite\">1674807484.0092986</price>\n" +
                "      <meal country=\"paint\">dish</meal>\n" +
                "      <ever>-376689439.5361357</ever>\n" +
                "      <market hung=\"anything\">combination</market>\n" +
                "      <leather rhythm=\"shells\">-1058206475</leather>\n" +
                "    </yesterday>\n" +
                "    <trade mouse=\"facing\">split</trade>\n" +
                "    <milk>-323706769</milk>\n" +
                "  </up>\n" +
                "  <grandfather>heart</grandfather>\n" +
                "  <tall>-251722200</tall>\n" +
                "  <pretty double=\"living\">500209685</pretty>\n" +
                "  <product>931190037.5590699</product>\n" +
                "</root>");
        this.model = model;

        showForm(EmbeddedViewForm.class, this.model);
    }

    @Action
    public void close() {
        exit();
    }

    @Action
    public void clickedHref() {
        FhLogger.info("HREF, " + this.model.getCurrentLink());
    }


}
