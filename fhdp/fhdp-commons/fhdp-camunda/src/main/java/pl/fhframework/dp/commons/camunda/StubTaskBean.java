package pl.fhframework.dp.commons.camunda;

import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Service;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/05/2020
 */
@Service
@Slf4j
public class StubTaskBean implements JavaDelegate {
//    @Autowired
//    ApplicationContext applicationContext;

    @Override
    public void execute(DelegateExecution delegateExecution) throws Exception {
//        FhLogger.info("Application context: {}", applicationContext.getBean("transactionManager"));
        log.info("Executed {} of process {}",
                delegateExecution.getCurrentActivityName(),
                delegateExecution.getProcessBusinessKey());
    }
}
