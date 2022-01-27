package pl.fhframework.dp.commons.camunda;

import org.camunda.bpm.BpmPlatform;
import org.camunda.bpm.ProcessEngineService;
import org.camunda.bpm.engine.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 21/04/2020
 */
@Configuration
@ComponentScan("pl.fhframework.dp.commons.camunda")
public class FhdpCamundaConfig {

    @Bean
    public ProcessEngineService processEngineService() {
        return BpmPlatform.getProcessEngineService();
    }


    @Bean(destroyMethod = "")
    public ProcessEngine processEngine(){
        return BpmPlatform.getDefaultProcessEngine();
    }


    @Bean
    public RepositoryService repositoryService(ProcessEngine processEngine) {
        return processEngine.getRepositoryService();
    }

    @Bean
    public RuntimeService runtimeService(ProcessEngine processEngine) {
        return processEngine.getRuntimeService();
    }

    @Bean
    public TaskService taskService(ProcessEngine processEngine) {
        return processEngine.getTaskService();
    }

    @Bean
    public HistoryService historyService(ProcessEngine processEngine) {
        return processEngine.getHistoryService();
    }

    @Bean
    public ManagementService managementService(ProcessEngine processEngine) {
        return processEngine.getManagementService();
    }

}
