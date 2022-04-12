package pl.fhframework.dp.commons.utils.guid;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 31/12/2020
 */
@Slf4j
public class GuidUtilTest {

    @Test
    public void stripToGuid() {
        String activityId = "Activity_0pkgb5u:a643421b-435c-11eb-9985-001a4a1967a8";
        String ret = GuidUtil.stripToGuid(activityId);
        log.info("Returned {}", ret);
    }

    @Test
    public void composeMessageId() {
        String activityId = "Activity_0uce44m";
        String processInstanceId = "44fa1d7a-b0ee-11ec-909e-005056010889";
        String messageId = GuidUtil.composeMessageId(activityId, processInstanceId);
        int length = messageId.length();
        int lengthProcInstId = processInstanceId.length();
        log.info("Returned: {}", messageId);
    }
}