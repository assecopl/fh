//package pl.fhframework.core.shutdown;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pl.fhframework.configuration.FHConfiguration;
//
//import java.util.Collection;
//
//@RestController
//public class InactiveClientKillerController {
//
//    @Autowired
//    private FHConfiguration fhConfiguration;
//
//    @Autowired
//    private InactiveClientKiller clientKiller;
//
//    @RequestMapping("/killInactive")
//    public Collection<String> killInactive() {
//        if (fhConfiguration.isDevModeActive()) {
//            return clientKiller.sendShudownToInactiveClients();
//        } else {
//            return null;
//        }
//    }
//}
