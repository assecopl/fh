package pl.fhframework.model.forms.utils;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.fhframework.SessionManager;
import pl.fhframework.events.ActionContext;

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

    @Value("${fh.web.highContrast.cssClass:fh-images-hidden}")
    @Getter
    public String cssClassForImagesHidden;

    public void setHighContrast() {
        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-high-contrast", Boolean.TRUE);
            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-high-contrast", Boolean.TRUE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }
    }

    public void setFontSize2() {

        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-size-2x", Boolean.TRUE);
                SessionManager.getUserSession().getAttributes().put("fh-size-4x", Boolean.FALSE);

            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-size-2x", Boolean.TRUE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }


    }

    public void setFontSize4() {
        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-size-2x", Boolean.FALSE);
                SessionManager.getUserSession().getAttributes().put("fh-size-4x", Boolean.TRUE);
            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-size-4x", Boolean.TRUE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }
    }

    public void setNormalFontSize() {
        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-size-4x", Boolean.FALSE);
                SessionManager.getUserSession().getAttributes().put("fh-size-2x", Boolean.FALSE);
            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-size-4x", Boolean.FALSE);
                map.put("fh-size-2x", Boolean.FALSE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }
    }

    public void setNormalContrast() {
        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-high-contrast", Boolean.FALSE);
            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-high-contrast", Boolean.FALSE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }
    }

    public void setImagesHidden() {
        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-images-hidden", Boolean.TRUE);
            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-images-hidden", Boolean.TRUE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }
    }

    public void setImagesShow() {
        if (SessionManager.getUserSession() != null) {
            if (SessionManager.getUserSession().getAttributes() != null) {
                SessionManager.getUserSession().getAttributes().put("fh-images-hidden", Boolean.FALSE);
            } else {
                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
                map.put("fh-images-hidden", Boolean.FALSE);
                SessionManager.getUserSession().setAttributes(map);
            }
        }
    }

    public Boolean isHighContrast() {

        if (SessionManager.getUserSession() != null && SessionManager.getUserSession().getAttributes() != null) {
            Boolean isHighContrast = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-high-contrast");
            return Boolean.TRUE.equals(isHighContrast);
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isImagesHidden () {
        if (SessionManager.getUserSession() != null && SessionManager.getUserSession().getAttributes() != null) {
            Boolean isImagesHidden = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-images-hidden");
            return Boolean.TRUE.equals(isImagesHidden);
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isFontSize2() {
        if (SessionManager.getUserSession() != null && SessionManager.getUserSession().getAttributes() != null) {
            Boolean isHighContrast = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-size-2x");
            return Boolean.TRUE.equals(isHighContrast);
        } else {
            return Boolean.FALSE;
        }
    }

    public Boolean isFontSize4() {
        if (SessionManager.getUserSession() != null && SessionManager.getUserSession().getAttributes() != null) {
            Boolean isHighContrast = (Boolean) SessionManager.getUserSession().getAttributes().get("fh-size-4x");
            return Boolean.TRUE.equals(isHighContrast);
        } else {
            return Boolean.FALSE;
        }
    }

}
