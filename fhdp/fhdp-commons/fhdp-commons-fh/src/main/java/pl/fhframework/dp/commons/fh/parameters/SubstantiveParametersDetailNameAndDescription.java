package pl.fhframework.dp.commons.fh.parameters;

import pl.fhframework.SessionManager;
import pl.fhframework.UserSession;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersDto;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersParameterDescription;
import pl.fhframework.dp.transport.dto.parameters.SubstantiveParametersParameterName;

import java.util.function.Supplier;

public class SubstantiveParametersDetailNameAndDescription {
    private final Supplier<SubstantiveParametersDto> dtoProvider;

    public SubstantiveParametersDetailNameAndDescription (Supplier<SubstantiveParametersDto> dtoProvider){
        this.dtoProvider = dtoProvider;
    }

    public String getDescription(){
        SubstantiveParametersParameterDescription description = getCurrentDescription(getCurrentLang());
        if (description!=null) {
            return description.getDescription();
        }else{
            return null;
        }
    }

    public void setDescription(String newValue){
        SubstantiveParametersParameterDescription description = getCurrentDescription(getCurrentLang());
        if (description!=null) {
            description.setDescription(newValue);
        }
    }

    public String getNameForSelectedLanguage(){
        SubstantiveParametersParameterName name = getCurrentName(getCurrentLang());
        if (name!=null) {
            return name.getName();
        }else{
            return null;
        }
    }

    public void setNameForSelectedLanguage(String newValue){
        SubstantiveParametersParameterName name = getCurrentName(getCurrentLang());
        if (name!=null) {
            name.setName(newValue);
        }
    }

    private String getCurrentLang(){
        UserSession session = SessionManager.getUserSession();
        if (session != null) {
            return session.getLanguage().getLanguage();
        } else {
            return "en";
        }
    }


    private SubstantiveParametersParameterDescription getCurrentDescription(String lang){
        return getDto().getParameterDescriptions().stream()
                .filter(desc -> lang.equalsIgnoreCase(desc.getLanguage()))
                .findFirst().orElseGet(() -> {
                    if (!"en".equals(lang)) {
                        SubstantiveParametersParameterDescription newOne = new SubstantiveParametersParameterDescription();
                        newOne.setLanguage(lang);
                        newOne.setDescription("");
                        getDto().getParameterDescriptions().add(newOne);
                        return newOne;
                    } else {
                        return null;
                    }
                });
    }

    private SubstantiveParametersParameterName getCurrentName(String lang){
        return getDto().getParameterNames().stream()
                .filter(name -> lang.equalsIgnoreCase(name.getLanguage()))
                .findFirst().orElseGet(() -> {
                    if (!"en".equals(lang)) {
                        SubstantiveParametersParameterName newOne = new SubstantiveParametersParameterName();
                        newOne.setLanguage(lang);
                        newOne.setName("");
                        getDto().getParameterNames().add(newOne);
                        return newOne;
                    } else {
                        return null;
                    }
                });
    }

    private SubstantiveParametersDto getDto(){
        return dtoProvider.get();
    }
}
