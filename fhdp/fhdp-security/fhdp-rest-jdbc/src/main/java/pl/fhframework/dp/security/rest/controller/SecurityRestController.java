package pl.fhframework.dp.security.rest.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import pl.fhframework.dp.security.services.PermissionService;
import pl.fhframework.dp.security.services.RoleService;
import pl.fhframework.dp.security.services.UserService;
import pl.fhframework.dp.transport.login.UserDto;
import pl.fhframework.dp.transport.permissions.PermissionDto;
import pl.fhframework.dp.transport.roles.RoleDto;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author <a href="mailto:jacek_borowiec@skg.pl">Jacek Borowiec</a>
 * @version :  $, :  $
 * @created 2019-07-25
 */
@Controller
@RequestMapping(value = "/sec")
@Slf4j
@RequiredArgsConstructor
public class SecurityRestController {

    private final UserService userService;
    private final RoleService roleService;
    private final PermissionService permissionService;

    // U S E R S
    //====================================================================================

    @GetMapping("/user-light/{login}")
    @ResponseBody
    public UserDto getUserData(@PathVariable String login) {
        log.info("getUserData. login: {}. ", login);
        return userService.findUser(login).orElse(null);
    }

    @GetMapping("/users")
    @ResponseBody
    public List<UserDto> getUsers() {
        log.info("getUsers.");
        return userService.findAllUsers();
    }

    // R O L E S
    //====================================================================================

    @GetMapping("/roles")
    @ResponseBody
    public ResponseEntity<List<String>> getAllRolesNames() {
        log.info("getAllRolesNames.");
        List<String> roles = roleService.findAllRoles().stream()
                .map(RoleDto::getRoleName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping(value = "/roles/all", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        log.info("getAllRoles.");
        return ResponseEntity.ok(roleService.findAllRoles());
    }

    @GetMapping(value = "/roles/{roleName}", produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public RoleDto findRoleByName(@PathVariable String roleName) {
        log.info("findRoleByName. roleName: {}.", roleName);
        return roleService.findRole(roleName).orElse(null);
    }

    @PostMapping(value = "/roles", consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ResponseEntity<RoleDto> saveRole(@RequestBody RoleDto roleDto) {
        log.info("saveRole.");
        return ResponseEntity.ok(roleService.saveRole(roleDto));
    }

    @DeleteMapping("/roles/{id}")
    @ResponseBody
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        log.info("deleteRole. id: {}.", id);
        Optional<RoleDto> role = roleService.findRole(id);
        if (role.isPresent()) {
            permissionService.deletePermissionsForRole(role.get().getRoleName());
            roleService.deleteRole(role.get());
        }
        return ResponseEntity.ok().build();
    }

    // P E R M I S S I O N S
    //====================================================================================

    @GetMapping("/permissions/all")
    @ResponseBody
    public ResponseEntity<List<PermissionDto>> getAllPermissions() {
        log.info("getAllPermissions.");
        return ResponseEntity.ok(permissionService.findAllPermissions());
    }

    @GetMapping("/permissions/{role}")
    @ResponseBody
    public ResponseEntity<List<PermissionDto>> getPermissionsForRole(@PathVariable String role) {
        log.info("getPermissionsForRole. role: {}.", role);
        return ResponseEntity.ok(permissionService.findPermissionsForRole(role));
    }

    @PostMapping(path = "/permissions", consumes = "application/json")
    @ResponseBody()
    public ResponseEntity<Void> savePermission(@RequestBody PermissionDto permission) {
        log.info("savePermission.");
        permissionService.savePermission(permission);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/permissions/{id}")
    @ResponseBody()
    public ResponseEntity<Void> deletePermission(@PathVariable Long id) {
        log.info("deletePermission. id: {}.", id);
        permissionService.deletePermission(id);
        return ResponseEntity.ok().build();
    }

}
