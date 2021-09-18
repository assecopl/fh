//package pl.fhframework.core.shutdown;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.SpringApplication;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ConfigurableApplicationContext;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import pl.fhframework.configuration.FHConfiguration;
//
//import java.util.concurrent.Executors;
//
//@RestController
//public class GracefulShutdownController {
//
//    @Autowired
//    private FHConfiguration fhConfiguration;
//
//    @Autowired
//    private ConfigurableApplicationContext context;
//
//    @RequestMapping("/gracefulShutdown")
//    public boolean gracefulShutdown() {
//        if (fhConfiguration.isDevModeActive()) {
//            sendShutdownSignal();
//            return true;
//        } else {
//            return false;
//        }
//    }
//
//    private void sendShutdownSignal() {
//        Executors.newSingleThreadExecutor().execute(context::close);
//    }
//}
