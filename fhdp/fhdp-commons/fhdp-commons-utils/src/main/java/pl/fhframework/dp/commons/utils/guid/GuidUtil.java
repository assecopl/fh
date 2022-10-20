package pl.fhframework.dp.commons.utils.guid;

/**
 * @author <a href="mailto:jacek.borowiec@asseco.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 31/12/2020
 */
public class GuidUtil {

    public static String stripToGuid(String activityId) {
        if(activityId.contains(":")) {
            return activityId.substring(activityId.indexOf(":") + 1, activityId.length());
        } else {
            return activityId;
        }
    }

    public static String composeMessageId(String activityId, String processInstanceId) {
        String actId = activityId.replace("Activity_", "");
        if(actId.length() > 7) {
            actId = actId.substring(0,7);
        }
        return (actId + "-" + processInstanceId).replace("-", "");
    }
}
