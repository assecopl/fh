package pl.fhframework.dp.commons.fh.utils;

import org.springframework.http.HttpHeaders;
import pl.fhframework.SessionManager;
import pl.fhframework.core.messages.MessagePopup;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.messages.ActionButton;
import pl.fhframework.model.forms.messages.Messages;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class FhUtils {

    public static Include createDynamicInclude(GroupingComponent<Component> destComponent, Object includedModel, String includedFormRef) {
        return createDynamicInclude(destComponent, includedModel, includedFormRef, null);
    }

    //TODO: - variants are not fully working
    public static Include createDynamicInclude(GroupingComponent<Component> destComponent, Object includedModel, String includedFormRef, String variantId) {
        for (Component c : new ArrayList<>(destComponent.getSubcomponents())) {
            destComponent.removeSubcomponent(c);
        }

        Include include = new Include(destComponent.getForm());
        destComponent.addSubcomponent(include);
        include.setGroupingParentComponent(destComponent);

        if (includedFormRef.endsWith(".frm")) {
            includedFormRef = includedFormRef.substring(0, includedFormRef.length() - 4);
        }

        include.setRefBinding(new pl.fhframework.binding.StaticBinding<>(includedFormRef));
        include.setModelValueBinding(new pl.fhframework.binding.StaticBinding<>(includedModel));

        include.init();

        if (variantId != null && !variantId.isEmpty()) {
            //includedComposite
            try {
                Field field = include.getClass().getDeclaredField("includedComposite");
                field.setAccessible(true);
                Form includedComposite = (Form) field.get(include);
                includedComposite.setVariant(variantId);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        
        return include;
    }

    public static void setCookieByKey(String key, String value) {
        HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(
                SessionManager.getUserSession().getDescription().getHandshakeHeaders()
        );

        List<String> cookiesStr = httpHeaders.get(HttpHeaders.COOKIE);
        if(cookiesStr != null) {
            String cookie = cookiesStr.get(0);
            String newCookies;
            if(cookie.contains(String.format("%s=", key))){
                String pattern = String.format("%s=(\\S+)(\\b)", key);
                newCookies = Pattern.compile(pattern).matcher(cookie).replaceFirst(
                        String.format("%s=%s", key, value)
                );
            } else {
                newCookies = String.format("%s; %s=%s", cookie, key, value);
            }
            httpHeaders.set(HttpHeaders.COOKIE, newCookies);
        }
    }

    public static String getCookieByKey(String key) {
        HttpHeaders httpHeaders = HttpHeaders.writableHttpHeaders(
                SessionManager.getUserSession().getDescription().getHandshakeHeaders()
        );
        List<String> cookiesStr = httpHeaders.get(HttpHeaders.COOKIE);

        if(cookiesStr != null) {
            String[] cookieSplit = cookiesStr.get(0).split("; ");

            for(String cookie: cookieSplit) {
                if(cookie.contains(key)) {
                    return cookie.split("=")[1];
                }
            }
        }

        return "";
    }

    public static FormElement findFormElement(IGroupingComponent<Component> rootElm, String startsWith) {
        List<FormElement> matches = new ArrayList<>();
        rootElm.doActionForEverySubcomponent((formElement) -> {
            if (formElement instanceof FormElement && formElement.getId() != null && formElement.getId().startsWith(startsWith)) {
                matches.add((FormElement)formElement);
            }
        });
        if (matches.isEmpty()) {
            throw new IllegalStateException("FormElement not found for:" + startsWith);
        } else if (matches.size() == 1) {
            return matches.get(0);
        } else {
            throw new IllegalStateException("Too many matching components:" + matches.size());
        }
    }

    public static boolean hasRole(String roleName) {
        return SessionManager.getSystemUser().getBusinessRoles().stream()
                .anyMatch(role -> role.getRoleName().equals(roleName));
    }


    public interface ClickConsumer {
        void accept();
    }

    public static void showConfirmDialogYesNo(String title, String message, ClickConsumer yes) {
        showConfirmDialogYesNo(title, message, yes, () -> {});
    }

    public static void showConfirmDialogYesNo(String title, String message, ClickConsumer yes, ClickConsumer no) {
        //TODO: i18n
        showConfirmDialogYesNo(title, message, "Yes", yes, "No", no);
    }

    public static void showConfirmDialogYesNo(String title, String message, String yesLabel, ClickConsumer yes, String noLabel, ClickConsumer no) {
        MessagePopup dialog = Messages.builder(SessionManager.getUserSession())
                .withDialogTitle(title)
                .withMessage(message)
                .withSeverityLevel(Messages.Severity.INFO)
                .withButtonAction(ActionButton.get(yesLabel, (v) -> {
                    Messages.close(v);
                    yes.accept();
                    }))
                .withButtonAction(ActionButton.get(noLabel, (v) -> {
                    Messages.close(v);
                    no.accept();
                    }))
                .build();
        dialog.setFormType(FormType.MODAL);
        dialog.showDialog();
    }
}
