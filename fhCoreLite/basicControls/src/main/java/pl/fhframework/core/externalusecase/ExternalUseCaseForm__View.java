package pl.fhframework.core.externalusecase;

import org.springframework.beans.factory.annotation.Autowired;
import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.CompiledBinding;
import pl.fhframework.core.dynamic.DynamicClassName;
import pl.fhframework.core.generator.CompiledClassesHelper;
import pl.fhframework.core.i18n.MessageService;
import pl.fhframework.core.rules.service.RulesService;
import pl.fhframework.format.FhConversionService;
import pl.fhframework.model.forms.Model;
import pl.fhframework.model.forms.OutputLabel;
import pl.fhframework.model.forms.Spacer;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.HorizontalAlign;
import pl.fhframework.model.forms.attribute.IconAlignment;
import pl.fhframework.model.forms.attribute.Layout;
import pl.fhframework.model.forms.attribute.VerticalAlign;

import java.time.Instant;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ExternalUseCaseForm__View extends ExternalUseCaseForm {
    @Autowired
    private FhConversionService __conversionService;
    @Autowired
    private MessageService __messagesService;
    @Autowired
    private RulesService __ruleService;

    Spacer a_spacer_1;

    OutputLabel a_outputLabel_1;

    Spacer a_spacer_2_1;

    Model a_model_1;
    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();

    static {
    }

    public static final Set<String> ____variants = new LinkedHashSet<>();


    public ExternalUseCaseForm__View() {
        initCmp_this();
        setupAccessibility();

    }

    private FhConversionService __getConversionService() {
        return __conversionService;
    }

    private MessageService __getMessageService() {
        return __messagesService;
    }

    private ExternalUseCaseForm__View getThisForm() {
        return this;
    }

    public static Map<DynamicClassName, Instant> getXmlTimestamps() {
        Map<DynamicClassName, Instant> timestamps = new HashMap<>();
        timestamps.put(DynamicClassName.forClassName("pl.fhframework.core.externalusecase.ExternalUseCaseForm"), Instant.ofEpochMilli(1557916458490L));
        timestamps.put(DynamicClassName.forClassName("java.lang.Void"), null);
        return timestamps;

    }

    private void initCmp_this() {
        this.setLabelModelBinding(new CompiledBinding<>(
                "{$.fh.core.externaluc.waiting}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getThis_labelModelBinding, /* getter */
                null /* setter */
        ));
        this.setDeclaredContainer("mainForm");
        this.setHideHeader(false);
        this.setLayout(Layout.VERTICAL);
        this.setModal(false);
        this.setFormType(FormType.MODAL);
        this.setModalSize(FormModalSize.SMALL);
        this.setId("ExternalUseCaseForm");

        a_spacer_1 = new Spacer(this);
        this.addSubcomponent(a_spacer_1);
        a_spacer_1.setGroupingParentComponent(this);
        initCmp_a_spacer_1(a_spacer_1);

        a_outputLabel_1 = new OutputLabel(this);
        this.addSubcomponent(a_outputLabel_1);
        a_outputLabel_1.setGroupingParentComponent(this);
        initCmp_a_outputLabel_1(a_outputLabel_1);

        a_spacer_2_1 = new Spacer(this);
        this.addSubcomponent(a_spacer_2_1);
        a_spacer_2_1.setGroupingParentComponent(this);
        initCmp_a_spacer_2_1(a_spacer_2_1);

        a_model_1 = new Model(this);
        this.setModelDefinition(a_model_1);
        initCmp_a_model_1(a_model_1);
    }


    // $.fh.core.externaluc.waiting
    private String getThis_labelModelBinding() {
        try {
            return __getMessageService().getAllBundles().getMessage("fh.core.externaluc.waiting");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getThis_labelModelBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_a_spacer_1(Spacer a_spacer_1) {
        a_spacer_1.setHeight("40px");
        a_spacer_1.setGroupingParentComponent(this);
    }

    private void initCmp_a_outputLabel_1(OutputLabel a_outputLabel_1) {
        a_outputLabel_1.setId("messageLabel");
        a_outputLabel_1.setIconAlignment(IconAlignment.BEFORE);
        a_outputLabel_1.setValueBinding(new CompiledBinding<>(
                "{$.fh.core.externaluc.external_uc_message}",
                null,
                String.class, /* target type */
                this::__getConversionService,
                this::getA_outputLabel_1_valueBinding, /* getter */
                null /* setter */
        ));
        a_outputLabel_1.setWidth("md-12");
        a_outputLabel_1.setHorizontalAlign(HorizontalAlign.CENTER);
        a_outputLabel_1.setVerticalAlign(VerticalAlign.MIDDLE);
        a_outputLabel_1.setGroupingParentComponent(this);
    }


    // $.fh.core.externaluc.external_uc_message
    private String getA_outputLabel_1_valueBinding() {
        try {
            return __getMessageService().getAllBundles().getMessage("fh.core.externaluc.external_uc_message");
        } catch (NullPointerException e) {
            if (CompiledClassesHelper.isLocalNullPointerException(e, getThisForm().getClass().getName(), "getA_outputLabel_1_valueBinding")) {
                return null;
            } else {
                throw e;
            }
        }
    }

    private void initCmp_a_spacer_2_1(Spacer a_spacer_2_1) {
        a_spacer_2_1.setHeight("60px");
        a_spacer_2_1.setGroupingParentComponent(this);
    }

    private void initCmp_a_model_1(Model a_model_1) {
    }

    private void setupAccessibility() {
        // defaultAvailability attributes of Variant elements
    }


}
