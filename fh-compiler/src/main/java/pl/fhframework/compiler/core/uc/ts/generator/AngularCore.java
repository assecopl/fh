package pl.fhframework.compiler.core.uc.ts.generator;

import pl.fhframework.compiler.core.generator.ts.TsDependency;

public class AngularCore {
    public static final TsDependency Component = TsDependency.builder().name("Component").appName("angular").module("core").build();
    public static final TsDependency OnDestroy = TsDependency.builder().name("OnDestroy").appName("angular").module("core").build();
    public static final TsDependency OnInit = TsDependency.builder().name("OnInit").appName("angular").module("core").build();
    public static final TsDependency NgModule = TsDependency.builder().name("NgModule").appName("angular").module("core").build();
    public static final TsDependency Injectable = TsDependency.builder().name("Injectable").appName("angular").module("core").build();
    public static final TsDependency FormBuilder = TsDependency.builder().name("FormBuilder").appName("angular").module("forms").build();
    public static final TsDependency FormGroup = TsDependency.builder().name("FormGroup").appName("angular").module("forms").build();
    public static final TsDependency FormsModule = TsDependency.builder().name("FormsModule").appName("angular").module("forms").build();
    public static final TsDependency ReactiveFormsModule = TsDependency.builder().name("ReactiveFormsModule").appName("angular").module("forms").build();
    public static final TsDependency BrowserModule = TsDependency.builder().name("BrowserModule").appName("angular").module("platform-browser").build();
    public static final TsDependency RouterModule = TsDependency.builder().name("RouterModule").appName("angular").module("router").build();
    public static final TsDependency Routes = TsDependency.builder().name("Routes").appName("angular").module("router").build();
    public static final TsDependency NgbModule = TsDependency.builder().name("NgbModule").appName("ng-bootstrap").module("ng-bootstrap").build();
}

