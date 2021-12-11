package pl.fhframework.compiler.core.model;

import org.springframework.stereotype.Service;
import pl.fhframework.compiler.core.model.meta.ClassTag;
import pl.fhframework.aspects.snapshots.SnapshotsModelAspect;
import pl.fhframework.core.logging.FhLogger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@Service
public class DynamicModelXmlManager {

    public static final Class JAXB_CONTENT_NODE_TAG = ClassTag.class;

    public ClassTag readDynamicClassModel(String fullClassPath) {
        ClassTag classTag = null;
        try {
            SnapshotsModelAspect.turnOff();
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_NODE_TAG);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            if (!fullClassPath.endsWith(DmoExtension.MODEL_FILENAME_EXTENSION)) {
                fullClassPath += DmoExtension.MODEL_FILENAME_EXTENSION;
            }

            classTag = (ClassTag) jaxbUnmarshaller.unmarshal(new File(fullClassPath));
        } catch (JAXBException e) {
            FhLogger.error(e);
        } finally {
            SnapshotsModelAspect.turnOn();

        }
        return classTag;
    }

    public void updateFile(ClassTag object, String pathToFile) {
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(JAXB_CONTENT_NODE_TAG);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            final File file = new File(pathToFile);
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }
            marshaller.marshal(object, new File(pathToFile));
        } catch (JAXBException e) {
            FhLogger.error(e);
        }
    }

    public boolean removeFile(String pathFileToDelete) {
        return new File(pathFileToDelete).delete();
    }

}
