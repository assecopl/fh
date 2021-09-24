package pl.fhframework.usecases.dynamic;


import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import pl.fhframework.XmlAttributeReader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DynamicActionHandler {
    public static class Action implements IUseCaseProcessElement, DynamicActivityHandler.IActivityContainer {
        @Getter
        @Setter(AccessLevel.PACKAGE)
        private String id;
        @Getter
        private String label;

        @Getter
        private UseCaseProcess useCaseProcess;

        private List<DynamicActivityHandler.Activity> _activities = new ArrayList<>();
        @Getter
        private List<DynamicActivityHandler.Activity> activities = Collections.unmodifiableList(_activities);
        private List<DynamicDeclarationHandler.RequiredAttribute> _requiredParameters = new ArrayList<>();
        @Getter
        private List<DynamicDeclarationHandler.RequiredAttribute> requiredParameters = Collections.unmodifiableList(_requiredParameters);
        public Action(String id, String label, UseCaseProcess useCaseProcess){
            this.id = id;
            this.label = label;
            this.useCaseProcess = useCaseProcess;
        }

        public Action(XmlAttributeReader xmlAttributeReader, UseCaseProcess useCaseProcess) {
            this(xmlAttributeReader.getAttributeValue("id"), xmlAttributeReader.getAttributeValue("label"), useCaseProcess);
        }

        public void addActivity(DynamicActivityHandler.Activity activity) {
            _activities.add(activity);
        }

        public void addRequiredAttribute(DynamicDeclarationHandler.RequiredAttribute parametr) {
            _requiredParameters.add(parametr);
        }
    }

    public static class Start extends Action {
        public Start(String id, String label, UseCaseProcess useCaseProcess) {
            super("start", label, useCaseProcess);
        }

        public Start(XmlAttributeReader xmlAttributeReader, UseCaseProcess useCaseProcess) {
            super(xmlAttributeReader, useCaseProcess);
            this.setId("start");
        }
    }

    public static class Output extends Action {
        String result;
        public Output(String id, String label, String result, UseCaseProcess useCaseProcess) {
            super(id, label, useCaseProcess);

        }

        public Output(XmlAttributeReader xmlAttributeReader, UseCaseProcess useCaseProcess) {
            super(xmlAttributeReader, useCaseProcess);
            this.result = xmlAttributeReader.getAttributeValue("result");
        }
    }

    public static class End extends Action {
        public End(String id, String label, UseCaseProcess useCaseProcess) {
            super(id, label, useCaseProcess);
        }

        public End(XmlAttributeReader xmlAttributeReader, UseCaseProcess useCaseProcess) {
            super(xmlAttributeReader, useCaseProcess);
        }
    }

    public static class SubUseCaseOutput implements IUseCaseProcessElement {
        @Getter
        String id;
        @Getter
        String label;
        @Getter
        DynamicActivityHandler.VisitSubUseCase transition;

        public SubUseCaseOutput(String id, String label, DynamicActivityHandler.VisitSubUseCase transition) {
            this.id = id;
            this.label = label;
            this.transition = transition;
        }

        public SubUseCaseOutput(XmlAttributeReader xmlAttributeReader, DynamicActivityHandler.VisitSubUseCase transition) {
            this.id = xmlAttributeReader.getAttributeValue("id");
            this.label = xmlAttributeReader.getAttributeValue("label");
            this.transition = transition;
            transition.getOutputs().add(this);
        }
    }
}
