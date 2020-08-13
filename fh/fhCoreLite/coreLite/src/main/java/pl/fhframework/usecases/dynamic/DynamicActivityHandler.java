package pl.fhframework.usecases.dynamic;

import lombok.Getter;

import pl.fhframework.core.FhException;
import pl.fhframework.XmlAttributeReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class DynamicActivityHandler {
    public static interface IActivityContainer {
        UseCaseProcess getUseCaseProcess();
    }

    public static abstract class Activity implements IUseCaseProcessElement {
        private static AtomicInteger counter = new AtomicInteger();
        @Getter
        private String id;

        @Getter
        private String label;

        @Getter
        private String ref;

        @Getter
        private IActivityContainer activityContainer;

        public Activity(XmlAttributeReader xmlAttributeReader, IActivityContainer activityContainer) {
            if (xmlAttributeReader != null) {
                this.id = xmlAttributeReader.getAttributeValue("id");
                this.label = xmlAttributeReader.getAttributeValue("label");
                this.ref = xmlAttributeReader.getAttributeValue("ref");
                this.activityContainer = activityContainer;

                if (ref == null) {
                    if (id == null) {
                        throw new FhException("Required parameter not specified: ref!");
                    }
                } else {
                    if (!ref.contains(".")) {
                        ref = activityContainer.getUseCaseProcess().getPackage() + "." + ref;
                    }
                }
                if (this.id == null) {
                    id = this.getClass().getSimpleName() + "_" + counter.getAndIncrement() + "_";
                    if (this.ref != null) {
                        id += "_" + ref;
                    }
                }
                if (label == null) {
                    this.label = this.id;
                }
            }
        }
    }

    public static class InitializeModel extends Activity implements IActivityContainer {

        public InitializeModel(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }

        @Override
        public UseCaseProcess getUseCaseProcess() {
            return this.getActivityContainer().getUseCaseProcess();
        }
    }

    public static class ReadModel extends Activity {
        public ReadModel(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }
    }

    public static class GetModel extends Activity {
        public GetModel(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }
    }

    public static class DisplayFormComponent extends Activity {
        public DisplayFormComponent(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }

        public String getFormControlFileName() {
            return this.getRef().substring(this.getRef().lastIndexOf("."));
        }
    }

    public static class GoTo extends Activity {
        public GoTo(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }
    }

    public static class Code extends Activity {
        private String code;

        public Code(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(null, action);
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }


    }

    public static class SaveModel extends Activity {
        public SaveModel(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }
    }

    public static class AdjustAttribute extends Activity {

        public AdjustAttribute(XmlAttributeReader xmlAttributeReader, IActivityContainer action) {
            super(xmlAttributeReader, action);
        }
    }

    public static class VisitSubUseCase extends Activity implements IActivityContainer {
        @Getter
        private List<DynamicActionHandler.SubUseCaseOutput> outputs = new ArrayList<>();

        public VisitSubUseCase(XmlAttributeReader xmlAttributeReader, DynamicActionHandler.Action action) {
            super(xmlAttributeReader, action);
        }

        @Override
        public UseCaseProcess getUseCaseProcess() {
            return this.getActivityContainer().getUseCaseProcess();
        }
    }


}
