package pl.fhframework.core.generator;

import lombok.Getter;
import pl.fhframework.core.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.UUID;
import java.util.stream.Collectors;

public class GenerationContext {

    public static final String INDENT = "    ";

    private static final class EmbeddedContext {

        private GenerationContext context;

        private int indent;

        public EmbeddedContext(GenerationContext context, int indent) {
            this.context = context;
            this.indent = indent;
        }
    }

    private final String uuid = UUID.randomUUID().toString();

    private StringBuilder code = new StringBuilder();

    private List<EmbeddedContext> embeddedGenerationContexts = new ArrayList<>();

    private Stack<CodeRange> rangesStack = new Stack<>();

    private List<CodeRange> rangesList = new ArrayList<>();

    @Getter
    private List<CodeRange> resolvedRangesList = new ArrayList<>();

    private boolean wasNewLine = true;

    public GenerationContext addCode(String codeFormat, String... args) {
        if (args.length > 0) {
            this.code.append(String.format(codeFormat, (Object[]) args));
        } else {
            this.code.append(codeFormat);
        }
        wasNewLine = false;
        return this;
    }

    public GenerationContext addLine(String codeFormat, String... args) {
        addCode(codeFormat, args);
        this.code.append('\n');
        return this;
    }

    public GenerationContext addLineWithIndent(int indent, String codeFormat, String... args) {
        addLine(indent(indent, codeFormat), args);
        return this;
    }

    public GenerationContext addLine() {
        this.code.append('\n');
        wasNewLine = true;
        return this;
    }

    public GenerationContext addLineIfNeeded() {
        if (!wasNewLine) {
            addLine();
        }
        return this;
    }

    public GenerationContext markLineNotNeeded() {
        wasNewLine = true;

        return this;
    }

    public GenerationContext addInlineSection(GenerationContext context, int indent) {
        embeddedGenerationContexts.add(new EmbeddedContext(context, indent));
        addCode(wrapUUID(context.uuid));
        return this;
    }

    public GenerationContext addSection(GenerationContext context, int indent) {
        addInlineSection(context, indent);
        addLine();
        return this;
    }

    public GenerationContext addSectionWithoutLine(GenerationContext context, int indent) {
        addInlineSection(context, indent);
        return this;
    }

    public GenerationContext registerDefferedSection() {
        GenerationContext defferedSection = new GenerationContext();
        addCode(wrapUUID(defferedSection.uuid));

        return defferedSection;
    }

    public GenerationContext addDefferedSection(GenerationContext context, int indent) {
        embeddedGenerationContexts.add(new EmbeddedContext(context, indent));
        return this;
    }

    public String resolveCode() {
        return resolveCodeBuffer().toString();
    }

    public void resolvePartially() {
        this.code = resolveCodeBuffer();
        this.rangesList.clear();
        this.rangesList.addAll(this.resolvedRangesList);
        embeddedGenerationContexts.clear();
    }

    private StringBuilder resolveCodeBuffer() {
        resolvedRangesList.clear();
        resolvedRangesList.addAll(rangesList.stream().map(CodeRange::of).collect(Collectors.toList()));

        StringBuilder outputCode = new StringBuilder(this.code);
        for (EmbeddedContext embedded : embeddedGenerationContexts) {
            String wrapedUUID = wrapUUID(embedded.context.uuid);

            int startLine = StringUtils.countLines(outputCode.subSequence(0, outputCode.indexOf(wrapedUUID)));

            String[] lines = embedded.context.resolveCode().split("\n");
            recalculateRanges(resolvedRangesList, lines.length, startLine, false);

            StringBuilder embeddedOutput = new StringBuilder();
            for (String line : lines) {
                embeddedOutput.append(indent(embedded.indent, line)).append("\n");
            }
            this.resolvedRangesList.addAll(recalculateRanges(embedded.context.resolvedRangesList, startLine - 1, 0, true));

            int replaceStart = outputCode.indexOf(wrapedUUID);
            int replaceEnd = replaceStart + wrapedUUID.length();
            outputCode = outputCode.replace(replaceStart, replaceEnd, embeddedOutput.toString());
        }

        return outputCode;
    }

    public boolean isEmpty() {
        return code.length() == 0;
    }

    private String wrapUUID(String uuid) {
        return "<<<" + uuid + ">>>";
    }

    public static String indent(int indent, String line) {
        return StringUtils.repeat(INDENT, indent) + line;
    }

    public void startRange(String name, String path) {
        if (!rangesStack.isEmpty()) {
            path = rangesStack.peek().getPath() + CodeRange.DELIMITER + path;
        }
        CodeRange codeRange = CodeRange.of(name, path);
        codeRange.setStart(countLines());

        rangesStack.push(codeRange);
        rangesList.add(codeRange);
    }

    public void endRange() {
        rangesStack.pop().setEnd(countLines() - (this.code.charAt(this.code.length() - 1) == '\n' ? 1 : 0));
    }

    private int countLines() {
        return StringUtils.countLines(this.code);
    }

    private List<CodeRange> recalculateRanges(List<CodeRange> codeRanges, int offset, int startLine, boolean copy) {
        List<CodeRange> toRecalculate = codeRanges;

        if (copy) {
            toRecalculate = codeRanges.stream().map(CodeRange::of).collect(Collectors.toList());
        }

        toRecalculate.forEach(codeRange -> {
            if (codeRange.getStart() >= startLine) {
                codeRange.setStart(codeRange.getStart() + offset);
            }
            if (codeRange.getEnd() >= startLine) {
                codeRange.setEnd(codeRange.getEnd() + offset);
            }
        });

        return toRecalculate;
    }

}
