<?xml version="1.0" encoding="UTF-8"?>
<Form xmlns="http://fh.asseco.com/form/1.0" id="SecurityDocs" label="{$.fh.docs.core.security_core_security}" container="mainForm">
    <AvailabilityConfiguration/>

    <TabContainer id="_Form_TabContainer">
        <Tab id="basicTab" label="{$.fh.docs.core.security_information}">
            <PanelGroup label="{$.fh.docs.core.security_basic_information}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel1">
                    {$.fh.docs.core.security_fh_core_provides_security_mechanism}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer1"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel2">
                    {$.fh.docs.core.security_system_function_represents_resource}
                    {$.fh.docs.core.security_annotation_systemfunction_can_be_placed}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer2"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel3">
                    {$.fh.docs.core.security_business_role_represents_business}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer3"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel4">
                    {$.fh.docs.core.security_permission_checks_are_injected}:
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer4"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel5">1. {$.fh.docs.core.security_when_initialized_menu_tree_is_loaded_based}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer5"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel6">2. {$.fh.docs.core.security_when_user_wants_to_execute_use_case}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer6"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel7">3. {$.fh.docs.core.security_when_user_wants_to_execute_action}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer7"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel8">4. {$.fh.docs.core.security_when_user_wants_to_execute_rule}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab1_PanelGroup1_Spacer8"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup1_OutputLabel9">5. {$.fh.docs.core.security_when_user_wants_to_execute_service}
                </OutputLabel>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.core.security_core_authorization_manager}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel1">
                    {$.fh.docs.core.security_fh_provides_core_authorization_manager}
                    {$.fh.docs.core.security_this_manager_is_fully_responsible}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab1_PanelGroup2_Spacer1"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup2_OutputLabel2">
                    {$.fh.docs.core.security_authorizationmanager_interface_is_presented_below}:
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab1_PanelGroup2_Spacer2"/>
                <InputText width="lg-8,md-10,sm-10,xs-12" availability="VIEW" rowsCount="35" id="_Form_TabContainer_Tab1_PanelGroup2_InputText">
                    <![CDATA[![ESCAPE[
/**
 * Core fh interfaces of security manager. Fh provides basic implementation named {@link
 * CoreAuthorizationManager}. This manager can be optional and work as a separate module that can be
 * plugged in.
 */
public interface AuthorizationManager extends PermissionsProvider {

    /**
     * Checks if given business roles(subject, probably user roles) has permission to action or use
     * case.
     *
     * @param businessRoles  - business roles
     * @param method - action
     * @return true if any role has access, false otherwise
     */
    boolean hasPermission(Collection<IBusinessRole> businessRoles, Method method);

    /**
     * Checks if given business roles(subject, probably user roles) has permission to action or use
     * case.
     *
     * @param businessRoles  - business roles
     * @param clazz - use case
     * @return true if any role has access, false otherwise
     */
    boolean hasPermission(Collection<IBusinessRole> businessRoles, Class clazz);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped system function.
     *
     * @deprecated use {@link #hasFunction(Collection, String, String)}
     *
     * @param businessRoles - business roles
     * @param functionName  - system function's name
     * @return true if any role has mapped system function, false otherwise
     */
    @Deprecated
    boolean hasFunction(Collection<IBusinessRole> businessRoles, String functionName);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped system function for given module.
     *
     * @param businessRoles - business roles
     * @param functionName  - system function's name
     * @param moduleUUID - a unique identification of an application module
     * @return true if any role has mapped system function, false otherwise
     */
    boolean hasFunction(Collection<IBusinessRole> businessRoles, String functionName, String moduleUUID);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped any of system functions.
     *
     * @deprecated use {@link #hasAnyFunction(Collection, Collection, String)}
     *
     * @param businessRoles - business roles
     * @param functionNames  - system function's names
     * @return true if any role has mapped to any system function, false otherwise
     */
    @Deprecated
    boolean hasAnyFunction(Collection<IBusinessRole> businessRoles, Collection<String> functionNames);

    /**
     * Checks if given business roles(subject, probably user roles) has mapped any of system functions.
     *
     * @param businessRoles - business roles
     * @param functionNames  - system function's names
     * @param moduleUUID - a unique identification of an application module
     * @return true if any role has mapped to any system function, false otherwise
     */
    boolean hasAnyFunction(Collection<IBusinessRole> businessRoles, Collection<String> functionNames, String moduleUUID);

    /**
     * If role based authorization is enabled (BusinessRoleProvider exists).
     *
     * @return if role based authorization is enabled (BusinessRoleProvider exists)
     */
    boolean isRoleBasedAuthorization();
    /**
     * Method provides possibility of clearing cached result of calculated permission info for
     * business roles.
     *
     * @param businessRoles - roles for which permissions will be removed from cache
     */
    void clearPermissions(Collection<IBusinessRole> businessRoles);

    /**
     * Maps given system functions from given module to business roles that provide them.
     * May return IBusinessRole.NONE if none of roles map to given system function.
     * @param systemFunctions system functions
     * @param moduleUUID module UUID
     * @return set of business roles
     */
    Set<String> mapSystemFunctionsToBusinessRoles(Set<String> systemFunctions, String moduleUUID);

    /**
     * Adds system function to authorization cache.
     * @param subsystem subsystem where the function was registered
     * @param functionName system function name
     */
    void registerSystemFunction(Subsystem subsystem, String functionName);

    /**
     * Gets all system functions for given subsystem.
     * @param subsystem representation of a subsystem.
     * @return set of system functions for given subsystem
     */
    Set<String> getSystemFunctionForSubsystem(Subsystem subsystem);

    /**
     * Returns all registered modules
     * @return set of modules
     */
    List<Module> getAllModules();

    /**
     * Returns all registered system functions
     * @return set of registered system functions
     */
    Set<Function> getAllSystemFunctions();

    /**
     * Invalidates information about permissions for given business role.
     * It makes authorization manager to recalculate permission for business
     * role during next access to use case.
     * @param businessRole business role
     */
    void invalidatePermissionCacheForRole(IBusinessRole businessRole);

    @Getter
    @EqualsAndHashCode(of = {"name", "moduleUUID"})
    class Function implements IFunction, Comparable<Function> {
        private String name;
        private String moduleUUID;
        private String moduleLabel;
        private boolean denial;

        private Function() {
        }

        public static Function of(String functionName, String moduleUUID) {
            return of(functionName, moduleUUID, ModuleRegistry.getModuleProductLabel(moduleUUID));
        }

        public static Function of(String functionName, String moduleUUID, String moduleLabel) {
            return of(functionName, moduleUUID, moduleLabel, false);
        }

        public static Function of(String functionName, String moduleUUID, String moduleLabel, boolean denial) {
            Function function = new Function();
            function.name = functionName;
            function.moduleUUID = moduleUUID;
            function.moduleLabel = moduleLabel;
            function.denial = denial;
            return function;
        }

        @Override
        public int compareTo(Function function) {
            if (function != null) {
                int result = name.compareTo(function.name);
                if (result != 0) return result;
                return moduleUUID.compareTo(function.moduleUUID);
            }
            return 0;
        }
    }

    @Getter
    @EqualsAndHashCode(of = "uuid")
    class Module implements IModule, Comparable<Module> {
        private String name;
        private String uuid;

        private Module() {
        }

        public static Module of(String name, String uuid) {
            Module module = new Module();
            module.name = name;
            module.uuid = uuid;
            return module;
        }

        @Override
        public int compareTo(Module module) {
            if (module != null) {
                return name.compareTo(module.name);
            }
            return 0;
        }

        @Override
        public String toString() {
            return name;
        }
    }

}
                        ]]]]>
                </InputText>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.core.security_allowing_guests_not_logged_in_users_access}" width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3">
            <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel1">
                {$.fh.docs.core.security_to_allow_not_logged_in_users}:
            </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab1_PanelGroup3_Spacer"/>
                <InputText width="md-12" availability="VIEW" rowsCount="1" id="_Form_TabContainer_Tab1_PanelGroup3_InputText">
                    <![CDATA[![ESCAPE[
fh.web.guests.allowed=true
                        ]]]]>
                </InputText>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab1_PanelGroup3_OutputLabel2">
                    {$.fh.docs.core.security_and_assign_guest_system_role_to_selected}.
                </OutputLabel>
            </PanelGroup>
        </Tab>
        <Tab id="newModuleId" label="{$.fh.docs.core.security_using_security_in_a_new_module}">
            <PanelGroup label="{$.fh.docs.core.security_introduction}" id="_Form_TabContainer_Tab2_PanelGroup1">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup1_OutputLabel1">
                    {$.fh.docs.core.security_on_starting_application_authorization}
                    {$.fh.docs.core.security_also_when_user_is_logged_in}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup1_Spacer"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup1_OutputLabel2">
                    {$.fh.docs.core.security_using_sam_module_administrator_can}
                </OutputLabel>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.core.security_instruction}" id="_Form_TabContainer_Tab2_PanelGroup2">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel1">
                    {$.fh.docs.core.security_new_module_must_fulfill_requirements}:
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer1"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel2">
                    1. {$.fh.docs.core.security_each_module_has_to_declare_enum}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer2"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel3">
                    a. {$.fh.docs.core.security_inside_the_enum_implementation_there}
                    {$.fh.docs.core.security_public_static_final_string}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer3"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel4">
                    b. {$.fh.docs.core.security_system_function_name_should_contains}
                    {$.fh.docs.core.security_pattern_modulename_functionalityname}
                    {$.fh.docs.core.security_separation_char_in_system_function}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer4"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel5">
                    c. {$.fh.docs.core.security_fields_declared_at_point}
                    {$.fh.docs.core.security_system_function_name}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer5"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel6">
                    d. {$.fh.docs.core.security_each_static_field_declared}
                    {$.fh.docs.core.security_fun_user_preview}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer6"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel7">
                    e.  {$.fh.docs.core.security_enums_declared_at_point_d}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer7"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel8">
                    f. {$.fh.docs.core.security_each_static_field_declared_at_point}
                    {$.fh.docs.core.security_systemfunction}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer8"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel9">
                    g. {$.fh.docs.core.security_it_is_able_to_declare_given}
                    {$.fh.docs.core.security_fun_user_management}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer9"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel10">
                    2. {$.fh.docs.core.security_module_sys_uuid}
                </OutputLabel>
                <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer10"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel11">
                    3. {$.fh.docs.core.security_also_in_module_sys}
                </OutputLabel>
                <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer11"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel12">
                    {$.fh.docs.core.security_example_implementation_of_system}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer12"/>
                <InputText width="lg-8,md-10,sm-10,xs-12" availability="VIEW" rowsCount="32" id="_Form_TabContainer_Tab2_PanelGroup2_InputText1">
                    <![CDATA[![ESCAPE[
public enum SAMSystemFunction implements ISystemFunctionId {

    // Enums
    FUN_SECURITY_USER_VIEW(SAMSystemFunction.SAM_SECURITY_USER_VIEW),
    FUN_SECURITY_USER_EDIT(SAMSystemFunction.SAM_SECURITY_USER_EDIT),
    FUN_SECURITY_ROLE_VIEW(SAMSystemFunction.SAM_SECURITY_ROLE_VIEW),
    FUN_SECURITY_ROLE_EDIT(SAMSystemFunction.SAM_SECURITY_ROLE_EDIT);

    // Functions names
    public static final String SAM_SECURITY_USER_VIEW = "sam/security/user/view";
    public static final String SAM_SECURITY_USER_EDIT = "sam/security/user/edit";
    public static final String SAM_SECURITY_ROLE_VIEW = "sam/security/role/view";
    public static final String SAM_SECURITY_ROLE_EDIT = "sam/security/role/edit";

    @Getter
    private String name;

    SAMSystemFunction(String name) {
        this.name = name;
    }

    @Override
    public ISystemFunctionId[] getFunctions() {
        return null; // NOTE: system functions hierarchy is embedded in a function name
    }

}
                        ]]]]>
                </InputText>
                <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer13"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab2_PanelGroup2_OutputLabel13">
                    {$.fh.docs.core.security_example_implementation_of_using}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab2_PanelGroup2_Spacer14"/>
                <InputText width="lg-8,md-10,sm-10,xs-12" availability="VIEW" rowsCount="21" id="_Form_TabContainer_Tab2_PanelGroup2_InputText2">
                    <![CDATA[![ESCAPE[
@UseCase
@SystemFunction(SAMSystemFunction.SAM_SECURITY_USER_VIEW)
public class UserAccountManagerUC implements IInitialUseCase {

    @Override
    public void start() {
        // show form
    }

    @Action
    public void searchUsers() {
        // do action
    }

    @Action
    @SystemFunction(SAMSystemFunction.SAM_SECURITY_USER_EDIT)
    public void deleteUserAccount() {
        // do restricted action
    }

}
                        ]]]]>
                </InputText>
            </PanelGroup>

            <!--<PanelGroup label="Using Manager as developer" width="md-12">-->
                <!--<OutputLabel width="md-12">-->
                    <!--Developer in use case class can verify if logged user has permission to any system function.-->
                    <!--In user session class there is a method named [b]hasAccessTo[/b], where method's argument is a function name (defined in system functions enum).-->
                <!--</OutputLabel>-->
                <!--<Spacer height="10px"/>-->
                <!--<Button label="Hidden message" onClick="hiddenAction"/>-->
                <!--<Spacer height="10px"/>-->
                <!--<InputText width="md-6" availability="VIEW" rowsCount="8">-->
                    <!--<![CDATA[![ESCAPE[-->
    <!--@Action-->
    <!--public void mockAction() {-->
        <!--String functionName = "someFunction";-->
        <!--if(getUserSession().hasAccessTo(functionName)) {-->
            <!--// code-->
        <!--}-->
    <!--}-->
                        <!--]]]]>-->
                <!--</InputText>-->
                <!--<InputText width="md-6" availability="VIEW" rowsCount="8">-->
                    <!--<![CDATA[![ESCAPE[-->
    <!--@Action-->
    <!--public void hiddenAction() {-->
        <!--if(getUserSession().hasAccessTo(DocsSystemFunction.FUN_HIDDEN_MESSAGE.getName())) {-->
            <!--Messages.showInfoMessage(getUserSession(), "Somehow you have access");-->
        <!--} else {-->
            <!--Messages.showInfoMessage(getUserSession(), "You don't have access");-->
        <!--}-->
    <!--}                ]]]]>-->
                <!--</InputText>-->

                <!--<OutputLabel width="md-12">By default, current core implementation of authorization manager, stores-->
                <!--result of calculated permission in cache. So if there are any changes in runtime made in mapping a business role-->
                <!--and system function, developer can clear stored cached for given business roles. It means that permissions checks will-->
                <!--be calculated again and possible new values will be stored in cache.</OutputLabel>-->
                <!--<Button label="Clear cache" onClick="clearCache"/>-->

            <!--</PanelGroup>-->

            <!--<PanelGroup label="Constrains" width="md-12">-->
                <!--<OutputLabel width="md-12">Business roles and system functions except for mapping, there is possibility of providing-->
                    <!--some constrain on that mapping. Constrain tests Predicate on UserSession, when manager checks for permission.-->
                    <!--Example below presents constrains for current selected user language:-->
                <!--</OutputLabel>-->
                <!--<Spacer height="10px"/>-->
                <!--<Button label="Constrain msg" onClick="constrainAction"/>-->
                <!--<Button label="Change language" onClick="changeLanguage"/>-->
            <!--</PanelGroup>-->
        </Tab>
        <Tab label="{$.fh.docs.core.security_application_privileges}" id="_Form_TabContainer_Tab3">
        <PanelGroup label="{$.fh.docs.core.security_privileges_introduction}" id="_Form_TabContainer_Tab3_PanelGroup">
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel1">
            {$.fh.docs.core.security_privileges_common}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer1"/>
        <InputText width="md-12" availability="VIEW" rowsCount="1" id="_Form_TabContainer_Tab3_PanelGroup_InputText1">
        <![CDATA[![ESCAPE[
fhframework.security.provider.application-privileges=security/app-privileges.yml
                    ]]]]>
        </InputText>
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel2">
            {$.fh.docs.core.security_privileges_selected_file}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer2"/>
        <InputText width="md-12" availability="VIEW" rowsCount="35" id="_Form_TabContainer_Tab3_PanelGroup_InputText2">
                <![CDATA[![ESCAPE[
privileges:

  # FH Designer
  - module:
      uuid: 06e72cae-5d0c-45de-a4e1-24c2825515d9
      permissions:
        - permission:
            type: ALLOW
            function: designer
        - permission:
            type: DENY
            function: designer/usecase

  # FH SAM
  - module:
      uuid: 6a01406b-62a8-49fd-af55-a79df19c8950
      permissions:
      - permission:
          type: ALLOW
          function: sam/security
      - permission:
          type: DENY
          function: sam/security/user/edit
      - permission:
          type: DENY
          function: sam/security/session

    # FH Scheduler
  - module:
     uuid: 1885ce88-365a-4abd-99f0-b2cc7ec662fb
     permissions:
     - permission:
         type: ALLOW
         function: scheduler/batch_job
                    ]]]]>
        </InputText>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer3"/>

        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel3">
            1. {$.fh.docs.core.security_privileges_module}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer4"/>
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel4">
            2. {$.fh.docs.core.security_privileges_uuid}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer5"/>
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel5">
            3. {$.fh.docs.core.security_privileges_permission}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer6"/>
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel6">
            4. {$.fh.docs.core.security_privileges_type}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer7"/>
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel7">
            5. {$.fh.docs.core.security_privileges_function}
        </OutputLabel>
        <Spacer width="md-12" height="20px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer8"/>
        <OutputLabel width="md-12" id="_Form_TabContainer_Tab3_PanelGroup_OutputLabel8">
            {$.fh.docs.core.security_privileges_allow_all}
        </OutputLabel>
        <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab3_PanelGroup_Spacer9"/>
            <InputText width="md-12" availability="VIEW" rowsCount="1" id="_Form_TabContainer_Tab3_PanelGroup_InputText3">
                <![CDATA[![ESCAPE[
allow-all: true
                    ]]]]>
            </InputText>
        </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.core.security.providers.tab}" id="_Form_TabContainer_Tab4">
            <PanelGroup label="{$.fh.docs.core.security.providers.description}" id="_Form_TabContainer_Tab4_PanelGroup1">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel1">
                    {$.fh.docs.core.security.providers.header}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer1"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel2">
                    {$.fh.docs.core.security.providers.types}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer2"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel3">
                    1. {$.fh.docs.core.security.providers.jdbc}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer3"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel4">
                    2. {$.fh.docs.core.security.providers.ldap}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer4"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel5">
                    3. {$.fh.docs.core.security.providers.saml}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer5"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel6">
                    4. {$.fh.docs.core.security.providers.remote}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer6"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel7">
                    5. {$.fh.docs.core.security.providers.rest}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer7"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel8">
                    6. {$.fh.docs.core.security.providers.cached}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer8"/>
                <InputText width="md-12" availability="VIEW" rowsCount="3" id="_Form_TabContainer_Tab4_PanelGroup1_InputText">
                    <![CDATA[![ESCAPE[
fhframework.security.provider.cached.user.0.username=admin
fhframework.security.provider.cached.user.0.firstName=Jan
fhframework.security.provider.cached.user.0.lastName=Kowalski
fhframework.security.provider.cached.user.0.pass=ENC(Ee+tuX15BNgFoYPYbxLw2Q==)
fhframework.security.provider.cached.user.0.roles=Administrator,User
                    ]]]]>
                </InputText>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer9"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel9">
                    {$.fh.docs.core.security.providers.permissions}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer10"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel10">
                    {$.fh.docs.core.security.providers.custom}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer11"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel11">
                    1. {$.fh.docs.core.security.providers.userAccountProvider}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer12"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel12">
                    2. {$.fh.docs.core.security.providers.businessRoleProvider}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup1_Spacer13"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup1_OutputLabel13">
                    3. {$.fh.docs.core.security.providers.securityProviderInitializer}
                </OutputLabel>
            </PanelGroup>
            <PanelGroup label="{$.fh.docs.core.security.providers.user_params}" id="_Form_TabContainer_Tab4_PanelGroup2">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup2_OutputLabel1">
                    {$.fh.docs.core.security.providers.user_params.header}
                </OutputLabel>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup2_OutputLabel2">
                    {$.fh.docs.core.security.providers.user_params.types}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup2_Spacer1"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup2_OutputLabel3">
                    1. {$.fh.docs.core.security.providers.user_params.remote}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup2_Spacer2"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab4_PanelGroup2_OutputLabel4">
                    2. {$.fh.docs.core.security.providers.user_params.rest}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab4_PanelGroup2_Spacer3"/>
                <InputText width="md-12" availability="VIEW" rowsCount="22" id="_Form_TabContainer_Tab4_PanelGroup2_InputText">
                    <![CDATA[![ESCAPE[
{
  "username": "admin",
  "firstName": "John",
  "lastName": "Doe",
  "password": "$2a$08$weKQfV.ODYgbTMY7xfwOPOpgSJwLRgF8BahJ94tOc55aNEIpLaWgW",
  "blocked": false,
  "deleted": false,
  "roles": [
      "Administrator"
  ],
  "attributes": {
      "index": 7096,
      "sessionId": "1cd4cd5d-7ba5-49b9-bf0a-3323388df957",
      "locale": "en_US",
      "userSiscProfile": {
          "userUuid": "admin",
          "firstName": "John",
          "surname": "Doe",
          "email": "john.doe@mail.com"
      }
  }
}

                    ]]]]>
                </InputText>
            </PanelGroup>
        </Tab>
        <Tab label="{$.fh.docs.core.security.permissions.tab}" id="_Form_TabContainer_Tab5">
            <PanelGroup label="{$.fh.docs.core.security.permissions.description}" id="_Form_TabContainer_Tab5_PanelGroup">
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel1">
                    {$.fh.docs.core.security.permissions.header}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer1"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel2">
                    {$.fh.docs.core.security.permissions.types}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer2"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel3">
                    1. {$.fh.docs.core.security.permissions.jdbc}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer3"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel4">
                    2. {$.fh.docs.core.security.permissions.standalone}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer4"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel5">
                    3. {$.fh.docs.core.security.permissions.rest}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer5"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel6">
                    {$.fh.docs.core.security.permissions.sam}
                </OutputLabel>
                <Spacer width="md-12" height="10px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer6"/>
                <OutputLabel width="md-12" id="_Form_TabContainer_Tab5_PanelGroup_OutputLabel7">
                    {$.fh.docs.core.security.permissions.custom}
                </OutputLabel>
                <Spacer width="md-12" height="5px" id="_Form_TabContainer_Tab5_PanelGroup_Spacer7"/>
            </PanelGroup>
        </Tab>
    </TabContainer>
    <Spacer height="10px" width="md-12" id="_Form_Spacer"/>
</Form>