
# Creating demo application
## 1. Goal
The following instruction describe how to create and run a simple application based on the **FH Framework library**, available in the public Maven repository:  https://mvnrepository.com/artifact/pl.fhframework

## 2. Configuration
### 2.1 Maven
- First you need to create a maven project:
~~~~
<groupId>com.example.fhdemo</groupId>
<artifactId>fh-demo</artifactId>
<version>0.0.1-SNAPSHOT</version>
<name>fh-demo</name>
~~~~

- Then you need to set **fh-compile-bom** as the parent component in the **pom.xml** file:
~~~~
<parent>
    <groupId>pl.fhframework</groupId>
    <artifactId>fh-compile-bom</artifactId>
    <version>4.6.15</version>
</parent>
~~~~

- Next you need to add dependency to **defaultApplication**, which is a basic implementation of the **FH Framework** application:
~~~~
<dependency>
    <groupId>pl.fhframework</groupId>
    <artifactId>defaultApplication</artifactId>
    <version>${fh.version}</version>
</dependency>
~~~~
- Next you need to add dependency to **security-persmission**, which is a basic implementation of the **FH Framework** application:
~~~~
     <dependency>
            <groupId>pl.fhframework.core.security.permission</groupId>
            <artifactId>permissionProvider-jdbc</artifactId>
            <version>${fh.version}</version>
        </dependency>

~~~~

Full **pom.xml** file content:
~~~~
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>pl.fhframework</groupId>
        <artifactId>fh-compile-bom</artifactId>
        <version>4.6.15</version>
    </parent>

    <groupId>com.example.fhdemo</groupId>
    <artifactId>fh-demo</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <name>fh-demo</name>

    <dependencies>
        <dependency>
            <groupId>pl.fhframework</groupId>
            <artifactId>defaultApplication</artifactId>
            <version>${fh.version}</version>
        </dependency>
		<dependency>
            <groupId>pl.fhframework.core.security.permission</groupId>
            <artifactId>permissionProvider-jdbc</artifactId>
            <version>${fh.version}</version>
        </dependency>
    </dependencies>

</project>
~~~~

### 2.2 Static elements
You can place your own two image files in the project resources in the location **/resources/static/img/**

- **logo_s.png** - logo graphic appearing on the login window
- **logo_w.png** - application name or logo graphic appearing in the upper left corner of the application

### 2.3 Application properties
You need to create **application.properties** file with following properties.

- Setting server port:
~~~~
# Server port
server.port=8090
~~~~

- Setting a local database connection:
~~~~
# Database configuration
spring.datasource.driverClassName=org.h2.Driver
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.datasource.url=jdbc:h2:file:./database/fhdemo-db
spring.datasource.user=fhdemo
spring.datasource.password=fhdemo
spring.datasource.min-pool-size=2
spring.datasource.max-pool-size=10
~~~~

- The following settings will cause that the user **admin** will be added to the system during the first launch of the application and will have one role **Administrator**. The basic configuration of the **FH Framework** application creates users and roles tables in the local database. It is also possible to connect the application to an external source of information about users and roles, e.g. LDAP, AD, REST.
~~~~
# Generate default users and roles when database is empty.
fhframework.security.provider.generate-default-data=true
# Default administration user login
fhframework.security.provider.default-admin-login=admin
# Default administration user password
fhframework.security.provider.default-admin-pass=admin
# Default administration role
fhframework.security.provider.default-admin-role=Administrator
~~~~

- Setting system use cases for our application. The following two use cases are responsible for displaying the application menu bar and the top navigation bar. Both use cases are available in **FH Framework**:  
~~~~
# System use case classes
system.usecases.classes=pl.fhframework.app.menu.MenuUC,pl.fhframework.app.menu.NavbarUC
# Pipe of additional layout names used in FH Framework. Each layout must have an HTML template file with a corresponding name
fhframework.layout.templates=full,panels
~~~~

### 2.4 New FH module
The created demo application will be a **FH Framework** module, therefore you should add the **module.sys** file in the resource directory, which will provide basic information about the module, i.e.:
- module unique name
- module label
- main java package of the module
- unique module UUID identifier

Full **/resources/module.sys** file content:
~~~~
<subsystem name="fh-demo" label="FH Demo" basePackage="com.example.fhdemo" productUUID="73f1ebe0-0dc9-4b6e-be99-3df8241e0ac2"/>
~~~~

## 3. Creating example use case
### 3.1 Form
First you need to create a form that will be displayed as part of the use case. The sample form will have only a field displaying the sample text and a button to close the use case.

Full **FhDemoForm.java** file content:
~~~~
package com.example.fhdemo;

import pl.fhframework.binding.ActionSignature;
import pl.fhframework.binding.CompiledActionBinding;
import pl.fhframework.binding.StaticBinding;
import pl.fhframework.model.forms.*;
import pl.fhframework.model.forms.attribute.FormModalSize;
import pl.fhframework.model.forms.attribute.FormType;
import pl.fhframework.model.forms.attribute.IconAlignment;

import java.util.LinkedHashSet;
import java.util.Set;

public class FhDemoForm extends Form<String> {

    public static final Set<ActionSignature> ____actions = new LinkedHashSet<>();
    static {
        ____actions.add(new ActionSignature("onClose"));
    }

    public FhDemoForm() {
        initComponents();
    }

    /** Initializes form components */
    private void initComponents() {
        this.setLabelModelBinding(new StaticBinding<>("FH Demo Use Case"));
        this.setDeclaredContainer("mainForm");
        this.setHideHeader(false);
        this.setFormType(FormType.STANDARD);
        this.setModalSize(FormModalSize.REGULAR);
        this.setXmlns("http://fh.asseco.com/form/1.0");
        this.setId("fHDemoForm");
        this.setInvisible(false);

        OutputLabel outputLabel = new OutputLabel(this);
        this.addSubcomponent(outputLabel);
        outputLabel.setGroupingParentComponent(this);
        initOutputLabel(outputLabel);

        Row row = new Row(this);
        this.addSubcomponent(row);
        row.setGroupingParentComponent(this);
        initRow(row);

        Button button = new Button(this);
        this.addSubcomponent(button);
        button.setGroupingParentComponent(this);
        initButton(button);

        Model model = new Model(this);
        this.setModelDefinition(model);
        initModel(model);
    }

    /** Initializes output label component */
    private void initOutputLabel(OutputLabel outputLabel) {
        outputLabel.setIconAlignment(IconAlignment.BEFORE);
        outputLabel.setValueBinding(new StaticBinding<>("[b]This is FH Demo use case...  [color=\'RED\'][icon=\'fas fa-thumbs-up\'][/color][/b]"));
        outputLabel.setWidth("md-12");
        outputLabel.setInvisible(false);
        outputLabel.setGroupingParentComponent(this);
    }

    /** Initializes row component */
    private void initRow(Row row) {
        row.setHeight("20px");
        row.setWidth("md-12");
        row.setInvisible(false);
        row.setGroupingParentComponent(this);
    }

    /** Initializes button component */
    private void initButton(Button button) {
        button.setOnClick(new CompiledActionBinding(
                "onClose", "onClose"));
        button.setLabelModelBinding(new StaticBinding<>("Close"));
        button.setWidth("md-2");
        button.setId("exitButton");
        button.setInvisible(false);
        button.setGroupingParentComponent(this);
    }

    /** Initializes form model */
    private void initModel(Model model) {
        model.setInvisible(false);
    }

}
~~~~

### 3.2 Use Case
Next you need to create a use case class that can be started from the applications menu. The use case will display the form from point 3.1 and will react to the action of closing the use case.

Full **FhDemoUC.java** file content:
~~~~
package com.example.fhdemo;

import pl.fhframework.annotations.Action;
import pl.fhframework.core.uc.IInitialUseCase;
import pl.fhframework.core.uc.UseCase;

@UseCase
public class FhDemoUC implements IInitialUseCase {

    @Override
    public void start() {
        showForm(FhDemoForm.class, "");
    }

    @Action
    public void onClose() {
        exit();
    }

}
~~~~

### 3.3 Application menu
In the last step you need to create the **menu.xml** file, in which we will specify that our use case will appear in the application menu tree. 

Full **/resource/menu.xml** file content:
~~~~
<UseCase ref="com.example.fhdemo.FhDemoUC" label="Demo use case"/>
~~~~

## 4. Building application
We build application using the **mvn** command in the main project directory:
~~~~
mvn clean install
~~~~

Below are the logs of building the application:
~~~~
d:\fh-demo>mvn clean install
[INFO] Scanning for projects...
[INFO]
[INFO] ---------------------< com.example.fhdemo:fh-demo >---------------------
[INFO] Building fh-demo 0.0.1-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO]
[INFO] --- maven-clean-plugin:3.1.0:clean (default-clean) @ fh-demo ---
[INFO] Deleting d:\fh-demo\target
[INFO]
[INFO] --- maven-resources-plugin:3.1.0:resources (default-resources) @ fh-demo ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] Copying 1 resource
[INFO] Copying 4 resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:compile (default-compile) @ fh-demo ---
[INFO] Changes detected - recompiling the module!
[INFO] Compiling 2 source files to d:\fh-demo\target\classes
[INFO] /d:/fh-demo/src/main/java/com/example/fhdemo/FhDemoForm.java: d:\fh-demo\src\main\java\com\example\fhdemo\FhDemoForm.java uses unchecked or unsafe operations.
[INFO] /d:/fh-demo/src/main/java/com/example/fhdemo/FhDemoForm.java: Recompile with -Xlint:unchecked for details.
[INFO]
[INFO] --- maven-resources-plugin:3.1.0:testResources (default-testResources) @ fh-demo ---
[INFO] Using 'UTF-8' encoding to copy filtered resources.
[INFO] skip non existing resourceDirectory d:\fh-demo\src\test\resources
[INFO]
[INFO] --- maven-compiler-plugin:3.8.0:testCompile (default-testCompile) @ fh-demo ---
[INFO] Nothing to compile - all classes are up to date
[INFO]
[INFO] --- maven-surefire-plugin:2.22.1:test (default-test) @ fh-demo ---
[INFO] No tests to run.
[INFO]
[INFO] --- maven-jar-plugin:3.1.0:jar (default-jar) @ fh-demo ---
[INFO] Building jar: d:\fh-demo\target\fh-demo-0.0.1-SNAPSHOT.jar
[INFO]
[INFO] --- maven-install-plugin:2.5.2:install (default-install) @ fh-demo ---
[INFO] Installing d:\fh-demo\target\fh-demo-0.0.1-SNAPSHOT.jar to C:\Users\tomasz.kozlowski\.m2\repository\com\example\fhdemo\fh-demo\0.0.1-SNAPSHOT\fh-demo-0.0.1-SNAPSHOT.jar
[INFO] Installing d:\fh-demo\pom.xml to C:\Users\tomasz.kozlowski\.m2\repository\com\example\fhdemo\fh-demo\0.0.1-SNAPSHOT\fh-demo-0.0.1-SNAPSHOT.pom
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  3.586 s
[INFO] Finished at: 2020-03-04T07:47:51+01:00
[INFO] ------------------------------------------------------------------------
~~~~

## 5. Running application
We run the application with following class as a startup class:
~~~~
pl.fhframework.app.DefaultApplication
~~~~

Below are the logs of running the application:
~~~~
                                                                                           
,------.,--.     ,------.                                                         ,--.     
|  .---'|  ,---. |  .---',--.--. ,--,--.,--,--,--. ,---. ,--.   ,--. ,---. ,--.--.|  |,-.  
|  `--, |  .-.  ||  `--, |  .--'' ,-.  ||        || .-. :|  |.'.|  || .-. ||  .--'|     /  
|  |`   |  | |  ||  |`   |  |   \ '-'  ||  |  |  |\   --.|   .'.   |' '-' '|  |   |  \  \  
`--'    `--' `--'`--'    `--'    `--`--'`--`--`--' `----''--'   '--' `---' `--'   `--'`--' 
                                                                                           
2020-03-04 09:48:17.554  INFO 32276 --- [           main] o.s.c.annotation.AutoProxyRegistrar      : AutoProxyRegistrar was imported but no annotations were found having both 'mode' and 'proxyTargetClass' attributes of type AdviceMode and boolean respectively. This means that auto proxy creator registration and configuration may not have occurred as intended, and components may not be proxied as expected. Check to ensure that AutoProxyRegistrar has been @Import'ed on the same class where these annotations are declared; otherwise remove the import of AutoProxyRegistrar altogether.
2020-03-04 09:48:17.571  INFO 32276 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Bootstrapping Spring Data repositories in DEFAULT mode.
2020-03-04 09:48:17.957  INFO 32276 --- [           main] .s.d.r.c.RepositoryConfigurationDelegate : Finished Spring Data repository scanning in 377ms. Found 4 repository interfaces.
2020-03-04 09:48:18.696  INFO 32276 --- [           main] ptablePropertiesBeanFactoryPostProcessor : Post-processing PropertySource instances
2020-03-04 09:48:18.751  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource configurationProperties [org.springframework.boot.context.properties.source.ConfigurationPropertySourcesPropertySource] to AOP Proxy
2020-03-04 09:48:18.751  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource servletConfigInitParams [org.springframework.core.env.PropertySource$StubPropertySource] to EncryptablePropertySourceWrapper
2020-03-04 09:48:18.751  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource servletContextInitParams [org.springframework.core.env.PropertySource$StubPropertySource] to EncryptablePropertySourceWrapper
2020-03-04 09:48:18.751  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource systemProperties [org.springframework.core.env.MapPropertySource] to EncryptableMapPropertySourceWrapper
2020-03-04 09:48:18.752  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource systemEnvironment [org.springframework.boot.env.SystemEnvironmentPropertySourceEnvironmentPostProcessor$OriginAwareSystemEnvironmentPropertySource] to EncryptableMapPropertySourceWrapper
2020-03-04 09:48:18.752  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource random [org.springframework.boot.env.RandomValuePropertySource] to EncryptablePropertySourceWrapper
2020-03-04 09:48:18.752  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource applicationConfig: [classpath:/application.properties] [org.springframework.boot.env.OriginTrackedMapPropertySource] to EncryptableMapPropertySourceWrapper
2020-03-04 09:48:18.752  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource class path resource [jasypt.properties] [org.springframework.core.io.support.ResourcePropertySource] to EncryptableMapPropertySourceWrapper
2020-03-04 09:48:18.752  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource class path resource [security-provider.properties] [org.springframework.core.io.support.ResourcePropertySource] to EncryptableMapPropertySourceWrapper
2020-03-04 09:48:18.752  INFO 32276 --- [           main] c.u.j.EncryptablePropertySourceConverter : Converting PropertySource class path resource [config/fh-application.properties] [org.springframework.core.io.support.ResourcePropertySource] to EncryptableMapPropertySourceWrapper
2020-03-04 09:48:19.128  INFO 32276 --- [           main] trationDelegate$BeanPostProcessorChecker : Bean 'org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration' of type [org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration$$EnhancerBySpringCGLIB$$e325ee22] is not eligible for getting processed by all BeanPostProcessors (for example: not eligible for auto-proxying)
2020-03-04 09:48:19.267  INFO 32276 --- [           main] c.u.j.r.DefaultLazyPropertyResolver      : Property Resolver custom Bean not found with name 'encryptablePropertyResolver'. Initializing Default Property Resolver
2020-03-04 09:48:19.269  INFO 32276 --- [           main] c.u.j.d.DefaultLazyPropertyDetector      : Property Detector custom Bean not found with name 'encryptablePropertyDetector'. Initializing Default Property Detector
2020-03-04 09:48:20.581  INFO 32276 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port(s): 8090 (http)
2020-03-04 09:48:20.600  INFO 32276 --- [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2020-03-04 09:48:20.601  INFO 32276 --- [           main] org.apache.catalina.core.StandardEngine  : Starting Servlet Engine: Apache Tomcat/9.0.12
2020-03-04 09:48:20.610  INFO 32276 --- [           main] o.a.catalina.core.AprLifecycleListener   : The APR based Apache Tomcat Native library which allows optimal performance in production environments was not found on the java.library.path: [C:\Program Files\Java\jdk1.8.0_212\bin;C:\Windows\Sun\Java\bin;C:\Windows\system32;C:\Windows;C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\jre64\\bin;C:\Program Files\JetBrains\IntelliJ IDEA 2019.1.3\jre64\\bin\server;c:\Program Files\Java\jdk1.8.0_212\bin;C:\Program Files (x86)\Common Files\Oracle\Java\javapath;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\iCLS\;C:\Program Files\Intel\Intel(R) Management Engine Components\iCLS\;C:\ProgramData\Oracle\Java\javapath;C:\Windows\system32;C:\Windows;C:\Windows\System32\Wbem;C:\Windows\System32\WindowsPowerShell\v1.0\;C:\Windows\System32\OpenSSH\;C:\Program Files (x86)\Intel\Intel(R) Management Engine Components\DAL;C:\Program Files\Intel\Intel(R) Management Engine Components\DAL;c:\Program Files (x86)\apache-maven-3.6.1\bin;C:\Program Files\TortoiseSVN\bin;C:\Program Files\TortoiseGit\bin;C:\Program Files\Git\cmd;c:\Program Files (x86)\Yarn\bin;C:\Program Files\PuTTY\;C:\Program Files\nodejs\;C:\Program Files\Redis\;C:\Program Files (x86)\GitExtensions\;C:\Users\tomasz.kozlowski\AppData\Local\Microsoft\WindowsApps;C:\Users\tomasz.kozlowski\AppData\Roaming\npm;.]
2020-03-04 09:48:20.801  INFO 32276 --- [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2020-03-04 09:48:20.801  INFO 32276 --- [           main] o.s.web.context.ContextLoader            : Root WebApplicationContext: initialization completed in 4357 ms
2020-03-04 09:48:24.642  WARN 32276 --- [           main] o.i.manager.DefaultCacheManager          : ISPN000435: Cache manager initialized with a default cache configuration but without a name for it. Set it in the GlobalConfiguration.
2020-03-04 09:48:24.790  INFO 32276 --- [           main] o.i.factories.GlobalComponentRegistry    : ISPN000128: Infinispan version: Infinispan 'Infinity Minus ONE +2' 9.4.0.Final
2020-03-04 09:48:24.989  INFO 32276 --- [           main] o.i.r.t.jgroups.JGroupsTransport         : ISPN000078: Starting JGroups channel APS00081178-MB
2020-03-04 09:48:39.860  WARN 32276 --- [           main] org.jgroups.protocols.UDP                : failed creating instance of bundler sender-sends-with-timer: java.lang.ClassNotFoundException: sender-sends-with-timer
2020-03-04 09:48:44.889  INFO 32276 --- [           main] org.infinispan.CLUSTER                   : ISPN000094: Received new cluster view for channel APS00081178-MB: [aps00081178-mb-7705|0] (1) [aps00081178-mb-7705]
2020-03-04 09:48:44.896  INFO 32276 --- [           main] o.i.r.t.jgroups.JGroupsTransport         : ISPN000079: Channel APS00081178-MB local address is aps00081178-mb-7705, physical addresses are [2a02:a310:23a:4000:6deb:8d16:9f24:beb2:63037]
Thanks for using Atomikos! Evaluate http://www.atomikos.com/Main/ExtremeTransactions for advanced features and professional support
or register at http://www.atomikos.com/Main/RegisterYourDownload to disable this message and receive FREE tips & advice
2020-03-04 09:48:45.970  INFO 32276 --- [           main] o.hibernate.jpa.internal.util.LogHelper  : HHH000204: Processing PersistenceUnitInfo [name: FhPU]
2020-03-04 09:48:46.054  INFO 32276 --- [           main] org.hibernate.Version                    : HHH000412: Hibernate Core {5.4.1.Final}
2020-03-04 09:48:46.187  INFO 32276 --- [           main] o.h.spatial.integration.SpatialService   : HHH80000001: hibernate-spatial integration enabled : true
2020-03-04 09:48:46.228  INFO 32276 --- [           main] o.hibernate.annotations.common.Version   : HCANN000001: Hibernate Commons Annotations {5.1.0.Final}
2020-03-04 09:48:46.407  INFO 32276 --- [           main] org.hibernate.dialect.Dialect            : HHH000400: Using dialect: org.hibernate.dialect.H2Dialect
2020-03-04 09:48:46.610  WARN 32276 --- [           main] o.h.b.i.InFlightMetadataCollectorImpl    : HHH000069: Duplicate generator name SEQUENCE_GENERATOR
2020-03-04 09:48:46.614  WARN 32276 --- [           main] o.h.b.i.InFlightMetadataCollectorImpl    : HHH000069: Duplicate generator name SEQUENCE_GENERATOR
2020-03-04 09:48:46.637  WARN 32276 --- [           main] o.h.b.i.InFlightMetadataCollectorImpl    : HHH000069: Duplicate generator name SEQUENCE_GENERATOR
2020-03-04 09:48:46.645  WARN 32276 --- [           main] o.h.b.i.InFlightMetadataCollectorImpl    : HHH000069: Duplicate generator name SEQUENCE_GENERATOR
2020-03-04 09:48:46.648  WARN 32276 --- [           main] o.h.b.i.InFlightMetadataCollectorImpl    : HHH000069: Duplicate generator name SEQUENCE_GENERATOR
2020-03-04 09:48:47.420  INFO 32276 --- [           main] o.h.e.t.j.p.i.JtaPlatformInitiator       : HHH000490: Using JtaPlatform implementation: [org.springframework.boot.orm.jpa.hibernate.SpringJtaPlatform]
Hibernate: create table FH_SEQUENCE (ID bigint not null, VERSION bigint not null, NAME varchar(255) not null, VALUE bigint, primary key (ID))
Hibernate: create table SEC_BUSINESS_ROLES (ID bigint not null, VERSION bigint not null, DESCRIPTION varchar(255), ROLE_NAME varchar(255) not null, ROOT_ROLE boolean, primary key (ID))
Hibernate: create table SEC_PERMISSIONS (ID bigint not null, VERSION bigint not null, BUSINESS_ROLE_NAME varchar(255) not null, CREATED_BY varchar(255), CREATION_DATE timestamp, DENIAL boolean, FUNCTION_NAME varchar(255) not null, MODULE_UUID varchar(255) not null, primary key (ID))
Hibernate: create table SEC_ROLE_INSTANCES (ID bigint not null, VERSION bigint not null, ASSIGNMENT_TIME timestamp not null, VALID_FROM timestamp, VALID_TO timestamp, BUSINESS_ROLE_ID bigint not null, USER_ACCOUNT_ID bigint, primary key (ID))
Hibernate: create table SEC_ROLE_ROLE (PARENT_ID bigint not null, CHILD_ID bigint not null)
Hibernate: create table SEC_USER_ACCOUNTS (ID bigint not null, VERSION bigint not null, BLOCKED boolean, BLOCKING_REASON varchar(255), DELETED boolean, EMAIL varchar(255) not null, FIRST_NAME varchar(255), LAST_NAME varchar(255), LOGIN varchar(255) not null, PASSWORD varchar(255) not null, primary key (ID))
Hibernate: create table SPATIAL_GEOMETRY (GEOMETRY_TYPE varchar(31) not null, ID bigint not null, VERSION bigint not null, geometry binary(255), primary key (ID))
Hibernate: create table WIDGET_INFO (ID bigint generated by default as identity, POS_X integer not null, POS_Y integer not null, REFERENCE varchar(255) not null, SIZE_X integer not null, SIZE_Y integer not null, USER_ID bigint not null, primary key (ID))
Hibernate: create table WIDGET_STATISTIC (ID bigint generated by default as identity, COUNT integer not null, NAME varchar(255) not null, primary key (ID))
Hibernate: alter table FH_SEQUENCE drop constraint if exists UK_lx83tpmvfg9jy6issbs4rc6j3
Hibernate: alter table FH_SEQUENCE add constraint UK_lx83tpmvfg9jy6issbs4rc6j3 unique (NAME)
Hibernate: alter table SEC_BUSINESS_ROLES drop constraint if exists UK_nr3xjtab98uxk5mlgmylu3bth
Hibernate: alter table SEC_BUSINESS_ROLES add constraint UK_nr3xjtab98uxk5mlgmylu3bth unique (ROLE_NAME)
Hibernate: alter table SEC_USER_ACCOUNTS drop constraint if exists UK_schwyxmxk0amdoxqru7r3nfdb
Hibernate: alter table SEC_USER_ACCOUNTS add constraint UK_schwyxmxk0amdoxqru7r3nfdb unique (LOGIN)
Hibernate: alter table WIDGET_STATISTIC drop constraint if exists UK_p42tol5qio2b2drpf2hf41in1
Hibernate: alter table WIDGET_STATISTIC add constraint UK_p42tol5qio2b2drpf2hf41in1 unique (NAME)
Hibernate: create sequence FH_SEQUENCE_ID_SEQ start with 1 increment by 50
Hibernate: create sequence SEC_BUSINESS_ROLES_ID_SEQ start with 1 increment by 50
Hibernate: create sequence SEC_PERMISSIONS_ID_SEQ start with 1 increment by 50
Hibernate: create sequence SEC_ROLE_INSTANCES_ID_SEQ start with 1 increment by 50
Hibernate: create sequence SEC_USER_ACCOUNTS_ID_SEQ start with 1 increment by 50
Hibernate: create sequence SPATIAL_GEO_ID_SEQ start with 1 increment by 50
Hibernate: alter table SEC_ROLE_INSTANCES add constraint FK1y8l2fwlfl23ynuxgi2daorn9 foreign key (BUSINESS_ROLE_ID) references SEC_BUSINESS_ROLES
Hibernate: alter table SEC_ROLE_INSTANCES add constraint FK5a8w443gimyug2ccnemfvblvx foreign key (USER_ACCOUNT_ID) references SEC_USER_ACCOUNTS
Hibernate: alter table SEC_ROLE_ROLE add constraint FKax0ieay702s8hii2q4okrehhq foreign key (CHILD_ID) references SEC_BUSINESS_ROLES
Hibernate: alter table SEC_ROLE_ROLE add constraint FK9e32qe6ul54871ddn84lh9a6x foreign key (PARENT_ID) references SEC_BUSINESS_ROLES
2020-03-04 09:48:47.496  INFO 32276 --- [           main] j.LocalContainerEntityManagerFactoryBean : Initialized JPA EntityManagerFactory for persistence unit 'FhPU'
2020-03-04 09:48:48.411  INFO 32276 --- [           main] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'characterEncodingFilter' to: [/*]
2020-03-04 09:48:48.412  INFO 32276 --- [           main] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'webMvcMetricsFilter' to: [/*]
2020-03-04 09:48:48.412  INFO 32276 --- [           main] .s.DelegatingFilterProxyRegistrationBean : Mapping filter: 'springSecurityFilterChain' to: [/*]
2020-03-04 09:48:48.413  INFO 32276 --- [           main] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'httpTraceFilter' to: [/*]
2020-03-04 09:48:48.413  INFO 32276 --- [           main] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'csrfTokenFilter' to: [/*]
2020-03-04 09:48:48.413  INFO 32276 --- [           main] o.s.b.w.servlet.FilterRegistrationBean   : Mapping filter: 'customSecurityFilter' to: [/*]
2020-03-04 09:48:48.413  INFO 32276 --- [           main] o.s.b.w.servlet.ServletRegistrationBean  : Servlet dispatcherServlet mapped to [/]
2020-03-04 09:48:49.495  INFO 32276 --- [           main] o.s.aop.framework.CglibAopProxy          : Unable to proxy interface-implementing method [public final org.springframework.web.servlet.HandlerExecutionChain org.springframework.web.servlet.handler.AbstractHandlerMapping.getHandler(javax.servlet.http.HttpServletRequest) throws java.lang.Exception] because it is marked as final: Consider using interface-based JDK proxies instead!
2020-03-04 09:48:49.495  INFO 32276 --- [           main] o.s.aop.framework.CglibAopProxy          : Unable to proxy interface-implementing method [public final void org.springframework.web.context.support.WebApplicationObjectSupport.setServletContext(javax.servlet.ServletContext)] because it is marked as final: Consider using interface-based JDK proxies instead!
2020-03-04 09:48:49.496  INFO 32276 --- [           main] o.s.aop.framework.CglibAopProxy          : Unable to proxy interface-implementing method [public final void org.springframework.context.support.ApplicationObjectSupport.setApplicationContext(org.springframework.context.ApplicationContext) throws org.springframework.beans.BeansException] because it is marked as final: Consider using interface-based JDK proxies instead!
Hibernate: select businessro0_.ID as ID1_1_, businessro0_.VERSION as VERSION2_1_, businessro0_.DESCRIPTION as DESCRIPT3_1_, businessro0_.ROLE_NAME as ROLE_NAM4_1_, businessro0_.ROOT_ROLE as ROOT_ROL5_1_ from SEC_BUSINESS_ROLES businessro0_
Hibernate: call next value for SEC_BUSINESS_ROLES_ID_SEQ
Hibernate: call next value for SEC_BUSINESS_ROLES_ID_SEQ
Hibernate: insert into SEC_BUSINESS_ROLES (VERSION, DESCRIPTION, ROLE_NAME, ROOT_ROLE, ID) values (?, ?, ?, ?, ?)
2020-03-04 09:48:50.393  INFO 32276 --- [           main] o.s.s.c.ThreadPoolTaskScheduler          : Initializing ExecutorService 'webSocketTaskScheduler'
Hibernate: select businessro0_.ID as ID1_1_, businessro0_.VERSION as VERSION2_1_, businessro0_.DESCRIPTION as DESCRIPT3_1_, businessro0_.ROLE_NAME as ROLE_NAM4_1_, businessro0_.ROOT_ROLE as ROOT_ROL5_1_ from SEC_BUSINESS_ROLES businessro0_ where upper(businessro0_.ROLE_NAME)=upper(?)
Hibernate: call next value for SEC_PERMISSIONS_ID_SEQ
Hibernate: call next value for SEC_PERMISSIONS_ID_SEQ
Hibernate: insert into SEC_PERMISSIONS (VERSION, BUSINESS_ROLE_NAME, CREATED_BY, CREATION_DATE, DENIAL, FUNCTION_NAME, MODULE_UUID, ID) values (?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: insert into SEC_PERMISSIONS (VERSION, BUSINESS_ROLE_NAME, CREATED_BY, CREATION_DATE, DENIAL, FUNCTION_NAME, MODULE_UUID, ID) values (?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: select count(*) as col_0_0_ from SEC_USER_ACCOUNTS useraccoun0_
Hibernate: select businessro0_.ID as ID1_1_, businessro0_.VERSION as VERSION2_1_, businessro0_.DESCRIPTION as DESCRIPT3_1_, businessro0_.ROLE_NAME as ROLE_NAM4_1_, businessro0_.ROOT_ROLE as ROOT_ROL5_1_ from SEC_BUSINESS_ROLES businessro0_ where upper(businessro0_.ROLE_NAME)=upper(?)
Hibernate: call next value for SEC_USER_ACCOUNTS_ID_SEQ
Hibernate: call next value for SEC_USER_ACCOUNTS_ID_SEQ
Hibernate: call next value for SEC_ROLE_INSTANCES_ID_SEQ
Hibernate: call next value for SEC_ROLE_INSTANCES_ID_SEQ
Hibernate: insert into SEC_USER_ACCOUNTS (VERSION, BLOCKED, BLOCKING_REASON, DELETED, EMAIL, FIRST_NAME, LAST_NAME, LOGIN, PASSWORD, ID) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate: insert into SEC_ROLE_INSTANCES (VERSION, ASSIGNMENT_TIME, BUSINESS_ROLE_ID, VALID_FROM, VALID_TO, ID) values (?, ?, ?, ?, ?, ?)
Hibernate: update SEC_ROLE_INSTANCES set USER_ACCOUNT_ID=? where ID=?
2020-03-04 09:48:50.666  INFO 32276 --- [           main] o.s.s.web.DefaultSecurityFilterChain     : Creating filter chain: any request, [org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@7ecac756, org.springframework.security.web.context.SecurityContextPersistenceFilter@61d18376, org.springframework.security.web.header.HeaderWriterFilter@1f2da372, org.springframework.web.filter.CorsFilter@719b8c4e, org.springframework.security.web.authentication.logout.LogoutFilter@744200de, pl.fhframework.accounts.SecurityFilter@407fda21, org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter@7a65bdb, org.springframework.security.web.session.ConcurrentSessionFilter@10125d66, org.springframework.security.web.authentication.www.BasicAuthenticationFilter@2596a640, org.springframework.security.web.savedrequest.RequestCacheAwareFilter@6c1c7ebc, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@60b7dc7c, org.springframework.security.web.authentication.AnonymousAuthenticationFilter@19599603, org.springframework.security.web.session.SessionManagementFilter@39738da1, org.springframework.security.web.access.ExceptionTranslationFilter@d5196fc, org.springframework.security.web.access.intercept.FilterSecurityInterceptor@1cb50215]
2020-03-04 09:48:52.607  INFO 32276 --- [           main] org.quartz.impl.StdSchedulerFactory      : Using default implementation for ThreadExecutor
2020-03-04 09:48:52.622  INFO 32276 --- [           main] org.quartz.core.SchedulerSignalerImpl    : Initialized Scheduler Signaller of type: class org.quartz.core.SchedulerSignalerImpl
2020-03-04 09:48:52.622  INFO 32276 --- [           main] org.quartz.core.QuartzScheduler          : Quartz Scheduler v.2.3.0 created.
2020-03-04 09:48:52.624  INFO 32276 --- [           main] org.quartz.simpl.RAMJobStore             : RAMJobStore initialized.
2020-03-04 09:48:52.625  INFO 32276 --- [           main] org.quartz.core.QuartzScheduler          : Scheduler meta-data: Quartz Scheduler (v2.3.0) 'quartzScheduler' with instanceId 'NON_CLUSTERED'
  Scheduler class: 'org.quartz.core.QuartzScheduler' - running locally.
  NOT STARTED.
  Currently in standby mode.
  Number of jobs executed: 0
  Using thread pool 'org.quartz.simpl.SimpleThreadPool' - with 10 threads.
  Using job-store 'org.quartz.simpl.RAMJobStore' - which does not support persistence. and is not clustered.

2020-03-04 09:48:52.625  INFO 32276 --- [           main] org.quartz.impl.StdSchedulerFactory      : Quartz scheduler 'quartzScheduler' initialized from an externally provided properties instance.
2020-03-04 09:48:52.625  INFO 32276 --- [           main] org.quartz.impl.StdSchedulerFactory      : Quartz scheduler version: 2.3.0
2020-03-04 09:48:52.625  INFO 32276 --- [           main] org.quartz.core.QuartzScheduler          : JobFactory set to: org.springframework.scheduling.quartz.SpringBeanJobFactory@2cae9603
2020-03-04 09:48:52.654  INFO 32276 --- [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 2 endpoint(s) beneath base path '/management'
2020-03-04 09:48:52.871  INFO 32276 --- [           main] o.s.s.quartz.SchedulerFactoryBean        : Starting Quartz Scheduler now
2020-03-04 09:48:52.873  INFO 32276 --- [           main] org.quartz.core.QuartzScheduler          : Scheduler quartzScheduler_$_NON_CLUSTERED started.
2020-03-04 09:48:52.909  INFO 32276 --- [           main] s.a.ScheduledAnnotationBeanPostProcessor : More than one TaskScheduler bean exists within the context, and none is named 'taskScheduler'. Mark one of them as primary or name it 'taskScheduler' (possibly as an alias); or implement the SchedulingConfigurer interface and call ScheduledTaskRegistrar#setScheduler explicitly within the configureTasks() callback: [defaultSockJsTaskScheduler, webSocketTaskScheduler]
2020-03-04 09:48:52.947  INFO 32276 --- [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port(s): 8090 (http) with context path ''
~~~~

## 6. Login
After starting the application go to the http://localhost:8090 and log in as a user **admin**, password **admin**