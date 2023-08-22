<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="PublisherDocForm" label="{$.fh.docs.core.publisher.menu}" container="mainForm" formType="STANDARD">

    <PanelGroup label="{$.fh.docs.core.publisher.basics}" width="md-12" id="_Form_PanelGroup1">
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.basics_description}" id="_Form_PanelGroup1_OutputLabel"/>
    </PanelGroup>
    <PanelGroup label="{$.fh.docs.core.publisher.usage}" width="md-12" id="_Form_PanelGroup2">
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.usage_model}" id="_Form_PanelGroup2_OutputLabel1"/>
        <Row height="10px"/>
        <InputText width="md-12" rowsCount="8" availability="VIEW" id="_Form_PanelGroup2_InputText1">
            <![CDATA[![ESCAPE[
@Getter
@Setter
public class ExampleDocMessage {

    private String sessionId;
    private String content;

}]]]]>
        </InputText>
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.usage_subscriber}" id="_Form_PanelGroup2_OutputLabel2"/>
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.usage_subscriber_get_topic}" id="_Form_PanelGroup2_OutputLabel3"/>
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.usage_subscriber_on_message}" id="_Form_PanelGroup2_OutputLabel4"/>
        <Row height="10px"/>
        <InputText width="md-12" rowsCount="26" availability="VIEW" id="_Form_PanelGroup2_InputText2">
<![CDATA[![ESCAPE[
@Service
@RequiredArgsConstructor
public class ExampleDocSubscriber implements MessageSubscriber {

    public final static String TOPIC = "docPubSub:example";

    private final IUserSessionService userSessionService;

    @Override
    public String getTopic() {
        return TOPIC;
    }

    @Override
    public void onMessage(Object object) {
        if (object instanceof ExampleDocMessage) {
            ExampleDocMessage message = ((ExampleDocMessage) object);
            userSessionService.sendMessage(
                Collections.singletonList(message.getSessionId()),
                "Info",
                message.getContent()
            );
        }
    }

}]]]]>
        </InputText>
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.usage_publish}" id="_Form_PanelGroup2_OutputLabel5"/>
        <Row height="10px"/>
        <InputText width="md-12" rowsCount="25" availability="VIEW" id="_Form_PanelGroup2_InputText3">
        <![CDATA[![ESCAPE[
@UseCase
@RequiredArgsConstructor
@UseCaseWithUrl(alias = "docs-publisher")
//@SystemFunction(DocsSystemFunction.FH_DOCUMENTATION_VIEW)
public class PublisherDocUC implements IInitialUseCase {

    private PublisherDocForm.Model model;

    private final MessagePublisher messagePublisher;

    @Override
    public void start() {
        model = new PublisherDocForm.Model();
        showForm(PublisherDocForm.class, model);
    }

    @Action
    public void onPublish() {
        ExampleDocMessage message = new ExampleDocMessage();
        message.setSessionId(SessionManager.getUserSession().getConversationUniqueId());
        message.setContent(model.getMessage());
        messagePublisher.publish(ExampleDocSubscriber.TOPIC, message);
    }

}]]]]>
        </InputText>
        <PanelGroup id="examplePG" borderVisible="true">
            <InputText width="md-6" label="{$.fh.docs.core.publisher.usage_example_message}" value="{message}" required="true" id="_Form_PanelGroup2_PanelGroup_InputText"/>
            <Row>
                <ValidateMessages width="md-6" level="error" componentIds="examplePG" id="_Form_PanelGroup2_PanelGroup_Row1_ValidateMessages"/>
            </Row>
            <Row>
                <Button width="md-2" label="{$.fh.docs.core.publisher.usage_example_send}" onClick="onPublish" id="_Form_PanelGroup2_PanelGroup_Row2_Button"/>
            </Row>
        </PanelGroup>
    </PanelGroup>
    <PanelGroup label="{$.fh.docs.core.publisher.config}" width="md-12" id="_Form_PanelGroup3">
        <OutputLabel width="md-12" value="{$.fh.docs.core.publisher.config_variants}" id="_Form_PanelGroup3_OutputLabel1"/>
        <OutputLabel width="md-12" value="1. {$.fh.docs.core.publisher.config_standalone}" id="_Form_PanelGroup3_OutputLabel2"/>
        <Row height="10px"/>
        <InputText width="md-12" rowsCount="6" availability="VIEW" id="_Form_PanelGroup3_InputText1">
            <![CDATA[![ESCAPE[
<dependency>
    <groupId>pl.fhframework</groupId>
    <artifactId>pubsub-standalone</artifactId>
    <version>${project.version}</version>
</dependency>]]]]>
        </InputText>
        <OutputLabel width="md-12" value="2. {$.fh.docs.core.publisher.config_cluster}" id="_Form_PanelGroup3_OutputLabel3"/>
        <Row height="10px"/>
        <InputText width="md-12" rowsCount="6" availability="VIEW" id="_Form_PanelGroup3_InputText2">
        <![CDATA[![ESCAPE[
<dependency>
    <groupId>pl.fhframework</groupId>
    <artifactId>pubsub-cluster</artifactId>
    <version>${project.version}</version>
</dependency>]]]]>
        </InputText>
        <Row height="10px"/>
        <InputText width="md-12" rowsCount="8" availability="VIEW" id="_Form_PanelGroup3_InputText3">
        <![CDATA[![ESCAPE[
# Redis server configuration
# Redis server host name
spring.redis.host=localhost
# Redis server port
spring.redis.port=6379
# Redis server user password
spring.redis.password=ENC(sCXGiXRyGtd8aNWASJKCAJYQF/nvhnQM)]]]]>
        </InputText>
    </PanelGroup>
</Form>