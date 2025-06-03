package pl.fhframework.compiler.forms;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.DiagnosticListener;
import javax.tools.JavaFileObject;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.Locale;

/**
 * Created by User on 2017-05-31.
 */
public class DynamicClassesCompilerDiagnostics implements DiagnosticListener<JavaFileObject> {
    private DiagnosticCollector<JavaFileObject> diagnosticCollector;

    DynamicClassesCompilerDiagnostics(DiagnosticCollector<JavaFileObject> diagnosticListener ){
        this.diagnosticCollector = diagnosticListener;
    }

    public void report(Diagnostic diagnostic) {
        diagnosticCollector.report(diagnostic);
    }

    public List<Diagnostic<? extends JavaFileObject>> getDiagnostics() {
        return diagnosticCollector.getDiagnostics();
    }

    public StringBuilder getDiagnosticsAsString(){
        StringBuilder sb = new StringBuilder();
        for (Diagnostic<? extends JavaFileObject> diagnostic : getDiagnostics()){
            String lsep = System.lineSeparator();
            sb
                    .append("Dynamic Class Compiler " + diagnostic.getKind().toString() + lsep)
                    .append("SOURCE:    " + (diagnostic.getSource()!=null ? diagnostic.getSource().getName() : "") +lsep)
                    .append("MESSAGE:   " + diagnostic.getMessage(Locale.getDefault())+lsep)
                    .append("CODE:      " + diagnostic.getCode()+lsep)
                    .append("LINE:      " + diagnostic.getLineNumber()+" COL:   " + diagnostic.getColumnNumber()+lsep)
                    .append(lsep)
                    ;
        }
        return sb;
    }

    public Writer getDiagnosticsAsStream(){
        return new StringWriter(){
            {write(getDiagnosticsAsString().toString());}
        };
    }
}
