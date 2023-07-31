package pl.fhframework.model.forms.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.SessionManager;

import java.util.*;

@Service
@RequiredArgsConstructor
public class WCAGService {

    @Value("${fh.web.highContrast.cssClass:fh-high-contrast}")
    @Getter
    public String cssClassForContrast;

    @Value("${fh.web.highContrast.cssClass:fh-size-2x}")
    @Getter
    public String cssClassForSize2;

    @Value("${fh.web.highContrast.cssClass:fh-size-4x}")
    @Getter
    public String cssClassForSize4;

    public void setHighContrast() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("fh-high-contrast", Boolean.TRUE);
        SessionManager.getUserSession().setAttributes(map);
    }

    public void setFontSize2() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("fh-size-2x", Boolean.TRUE);
        SessionManager.getUserSession().setAttributes(map);
    }

    public void setFontSize4() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("fh-size-4x", Boolean.TRUE);
        SessionManager.getUserSession().setAttributes(map);
    }

    public void setNormalContrast() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("fh-size-4x", Boolean.FALSE);
        map.put("fh-size-2x", Boolean.FALSE);
        SessionManager.getUserSession().setAttributes(map);
    }

    public void setNormalFontSize() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("fh-high-contrast", Boolean.FALSE);
        SessionManager.getUserSession().setAttributes(map);
    }

    public Boolean isHighContrast() {

        if (SessionManager.getUserSession() != null) {
            Boolean isHighContrast = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-high-contrast");
            return Boolean.TRUE.equals(isHighContrast);
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isFontSize2() {
        if (SessionManager.getUserSession() != null) {
            Boolean isHighContrast = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-size-2x");
            return Boolean.TRUE.equals(isHighContrast);
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isFontSize4() {
        if (SessionManager.getUserSession() != null) {
            Boolean isHighContrast = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-size-4x");
            return Boolean.TRUE.equals(isHighContrast);
        } else {
            return Boolean.FALSE;
        }
    }

}
