package pl.fhframework.usecases.dynamic;

import lombok.Getter;

import pl.fhframework.XmlAttributeReader;


public class DynamicDeclarationHandler {


    public static class DeclareModel implements IUseCaseProcessElement {
        @Getter
        private String id;
        @Getter
        private String type;

        @Getter
        private String label;

        public DeclareModel(XmlAttributeReader xmlAttributeReader) {
            this.id = xmlAttributeReader.getAttributeValue("id");
            this.type = xmlAttributeReader.getAttributeValue("type");
            this.label = xmlAttributeReader.getAttributeValue("label");
            if (label == null){
                this.label = this.id;
            }
        }
    }

    public static class DeclareAttribute implements IUseCaseProcessElement {
        @Getter
        private String id;
        @Getter
        private String type;
        @Getter
        private String label;

        public DeclareAttribute(XmlAttributeReader xmlAttributeReader) {
            this.id = xmlAttributeReader.getAttributeValue("id");
            this.type = xmlAttributeReader.getAttributeValue("type");
            this.label = xmlAttributeReader.getAttributeValue("label");
            if (label == null){
                this.label = this.id;
            }
        }
    }

    public static class RequiredAttribute implements IUseCaseProcessElement {
        @Getter
        private String id;
        @Getter
        private String type;
        @Getter
        private String label;

        public RequiredAttribute(XmlAttributeReader xmlAttributeReader) {
            this.id = xmlAttributeReader.getAttributeValue("id");
            this.type = xmlAttributeReader.getAttributeValue("type");
            this.label = xmlAttributeReader.getAttributeValue("label");
            if (label == null){
                this.label = this.id;
            }
        }
    }


}
