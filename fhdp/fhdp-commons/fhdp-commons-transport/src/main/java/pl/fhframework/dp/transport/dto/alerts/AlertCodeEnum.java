package pl.fhframework.dp.transport.dto.alerts;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 23/03/2021
 */
@Getter
public enum AlertCodeEnum {
    NoChannel(Arrays.asList("Clearance manager")),
    NotifyTraderAboutControl(Arrays.asList("Clearance manager")),
    ReleaseFailed(Arrays.asList("Clearance manager")),
    NegativeITISEUAnswerToCVR(Arrays.asList("Clearance manager")),
    AutoReleaseLimitExhausted(Arrays.asList("Clearance manager"));

    private List<String> roles;

    AlertCodeEnum(List<String> roles) {
        this.roles = roles;
    }

    public String getDescription() {
        return "$.enum." + getClass().getName() + "." + name() + ".description";
    }

    public String getGuidelines() {
        return "$.enum." + getClass().getName() + "." + name() + ".guidelines";
    }

    public String getCodeName() {
        return "$.enum." + getClass().getName() + "." + name() + ".codeName";
    }
}
