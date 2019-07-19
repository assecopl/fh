package pl.fhframework.core.util;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class TemplateVariablesExpander {

    @Data
    @AllArgsConstructor
    public static class LoopingSection {
        private boolean shouldBeOn;
        private List<Map<String, Object>> iterationsVars;
    }

    public String expand(String template, Map<String, Object> vars) {
        return expand(template, vars, Collections.emptyMap());
    }

    public String expand(String template, Map<String, Object> vars, Map<String, Boolean> sections) {
        return expand(template, vars, sections, Collections.emptyMap());
    }

    public String expand(String template, Map<String, Object> vars, Map<String, Boolean> sections, Map<String, LoopingSection> loopingSections) {
        if (template == null) {
            template = "";
        }

        template = expandVariables(template, vars);
        for (Map.Entry<String, Boolean> section : sections.entrySet()) {
            template = optionalSection(template, section.getKey(), section.getValue(), Arrays.asList(Collections.emptyMap()));
        }
        for (Map.Entry<String, LoopingSection> section : loopingSections.entrySet()) {
            template = optionalSection(template, section.getKey(), section.getValue().shouldBeOn, section.getValue().iterationsVars);
        }
        return template;
    }

    private String optionalSection(String template, String sectionName, boolean shouldBeOn, List<Map<String, Object>> iterationsVars) {
        String result = template;
        result = doOptionalSection(result, sectionName, shouldBeOn, iterationsVars);
        result = doOptionalSection(result, "!" + sectionName, !shouldBeOn, iterationsVars);
        return result;
    }

    private String doOptionalSection(String template, String sectionName, boolean shouldBeOn, List<Map<String, Object>> iterationsVars) {
        // -1 is so the empty lines aren't trimmed
        List<String> lines = Arrays.asList(template.split("\n", -1));
        List<String> result = new ArrayList<>();

        boolean inSection = false;

        String opening = "{" + sectionName + "?}";
        String closing = "{/" + sectionName + "?}";

        List<String> sectionLines = new ArrayList<>();
        for (String line : lines) {
            if (line.startsWith(opening)) {
                inSection = true;
            } else if (line.startsWith(closing)) {
                inSection = false;
                if (shouldBeOn) {
                    // for each iteration
                    for (Map<String, Object> iterationVars : iterationsVars) {
                        // for each sesion line
                        for (String sectionLine : sectionLines) {
                            result.add(expandVariables(sectionLine, iterationVars));
                        }
                    }
                }
                sectionLines.clear();
            } else {
                if (inSection) {
                    sectionLines.add(line);
                } else {
                    result.add(line);
                }
            }
        }
        return result.stream().collect(Collectors.joining("\n"));
    }

    private String expandVariables(String template, Map<String, Object> vars) {
        for (Map.Entry<String, Object> var : vars.entrySet()) {
            template = expandVariable(template, var.getKey(), var.getValue());
        }
        return template;
    }

    private String expandVariable(String template, String var, Object value) {
        String searchString = "{" + var + "}";
        return template.replace(searchString, value == null ? "" : "" + value);
    }
}
