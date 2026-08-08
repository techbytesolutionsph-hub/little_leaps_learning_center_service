package ph.com.lllc.service.api.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.com.lllc.dto.admin.AppUserResponse;
import ph.com.lllc.dto.admin.CreateUserRequest;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.entity.user.common.*;
import ph.com.lllc.entity.user.staff.timesheet.AppWeeklyTimesheet;
import ph.com.lllc.enums.TimesheetStatus;
import ph.com.lllc.enums.UserRole;
import ph.com.lllc.enums.UserStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.*;
import ph.com.lllc.service.db.SequenceGeneratorService;
import ph.com.lllc.service.util.IdGeneratorUtils;
import ph.com.lllc.service.util.PasswordGeneratorService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.util.BCryptUtils;
import ph.com.lllc.util.ObjectUtils;

import java.util.*;

@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final AppUserRepository appUserRepository;
    private final AppUserRoleRepository appUserRoleRepository;
    private final AppRoleMappingRepository appRoleMappingRepository;
    private final AppRolePermissionRepository appRolePermissionRepository;
    private final AppPermissionRepository appPermissionRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final PasswordGeneratorService passwordGeneratorService;
    private final IdGeneratorUtils idGeneratorUtils;
    private final LoggingService loggingService;
    private final BCryptUtils encoder;

    public String getIdRunningSequence(String dateHired) throws ServiceException {
        long nextUserSeq = sequenceGeneratorService.getStaffIdNextSequence();
        return idGeneratorUtils.generateEmployeeId(dateHired, nextUserSeq);
    }

    @Transactional
    public CommonResponse createUserAccount(String uuid, CreateUserRequest request) throws ServiceException {

        /* Check existing email */
        if (appUserRepository.existsByEmail(request.getEmail())) {
            loggingService.error(uuid, getClass().getName(), "Email already exists.", HttpStatus.CONFLICT.value());
            throw new ServiceException(HttpStatus.CONFLICT.value(), "Email already exists.");
        }

        /* Check existing username */
        if (appUserRepository.existsByUsername(request.getUsername())) {
            loggingService.error(uuid, getClass().getName(), "Username already exists.", HttpStatus.CONFLICT.value());
            throw new ServiceException(HttpStatus.CONFLICT.value(), "Username already exists.");
        }

        /* Create User Account */
        AppUser appUser = new AppUser();
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(encoder.encodePassword(request.getPassword()));
        appUser.setLastPassword(request.getPassword());
        appUser.setStatus(request.getStatus());
        appUser.setProfileImageUrl(request.getProfileImageUrl());
        appUser.setActive(true);

        /* Create User Role */
        AppUserRole appUserRole = AppUserRole.builder()
                .userRole(request.getRole())
                .appUser(appUser)
                .build();

        List<AppRolePermission> rolePermissions = new ArrayList<>();

        /* Load role mappings */
        List<AppRoleMapping> roleMappings =
                appRoleMappingRepository.findByRole(request.getRole().name());

        for (AppRoleMapping mapping : roleMappings) {

            /*
             * ALWAYS CREATE A NEW PERMISSION
             */
            AppPermission permission = new AppPermission();
            permission.setPermissionCode(mapping.getPermissionCode());
            permission.setDescription(mapping.getDescription());

            permission = appPermissionRepository.save(permission);

            /*
             * CREATE ROLE PERMISSION
             */
            AppRolePermission rolePermission = new AppRolePermission();
            rolePermission.setUserRole(appUserRole);
            rolePermission.setPermission(permission);

            rolePermissions.add(rolePermission);
        }

        appUserRole.setRolePermissions(rolePermissions);

        appUser.setUserRole(new ArrayList<>());
        appUser.getUserRole().add(appUserRole);

        appUserRepository.save(appUser);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("appUser", appUser);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("User account created successfully!")
                .responseBody(responseBody)
                .build();
    }

    public String generateTemporaryPassword(){
        return passwordGeneratorService.generatePassword(16);
    }

    public List<AppUserResponse> getAllUsers(){
        return ObjectUtils.copyListAs(appUserRepository.findAll(
                Sort.by(Sort.Direction.DESC, "appUserId")
        ), AppUserResponse.class);
    }

    public AppUserResponse findByUsername(String username) throws ServiceException {

        /* Find user */
        AppUser appUser = appUserRepository.findUserByUsername(username)
                .orElseThrow(() -> new ServiceException(
                        HttpStatus.NOT_FOUND.value(),
                        "User not found: " + username));

        AppUserResponse response = ObjectUtils.copyAs(appUser, AppUserResponse.class);

        if (appUser.getUserRole() != null && !appUser.getUserRole().isEmpty()) {
            response.setRole(appUser.getUserRole().get(0).getUserRole());
        }

        return response;
    }

    @Transactional
    public CommonResponse updateUserAccount(String uuid, CreateUserRequest request) throws ServiceException {

        /* Find existing user */
        AppUser appUser = appUserRepository.findUserByUsername(request.getUsername())
                .orElseThrow(() -> new ServiceException(
                        HttpStatus.NOT_FOUND.value(),
                        "User not found: " + request.getUsername()));

        /* Check duplicate email */
        if (!Objects.equals(appUser.getEmail(), request.getEmail())) {

            Optional<AppUser> existingEmail =
                    appUserRepository.findUserByEmail(request.getEmail());

            if (existingEmail.isPresent()
                    && !existingEmail.get().getAppUserId().equals(appUser.getAppUserId())) {

                loggingService.error(
                        uuid,
                        getClass().getName(),
                        "Email already exists.",
                        HttpStatus.CONFLICT.value());

                throw new ServiceException(
                        HttpStatus.CONFLICT.value(),
                        "Email already exists.");
            }
        }

        /* Update basic information */
        appUser.setUsername(request.getUsername());
        appUser.setEmail(request.getEmail());
        appUser.setPassword(encoder.encodePassword(request.getPassword()));
        appUser.setLastPassword(request.getPassword());
        appUser.setStatus(request.getStatus());

        /* Get current role */
        AppUserRole appUserRole = appUser.getUserRole()
                .stream()
                .findFirst()
                .orElseThrow(() -> new ServiceException(
                        HttpStatus.NOT_FOUND.value(),
                        "User role not found."));

        /* Role changed */
        if (!appUserRole.getUserRole().equals(request.getRole())) {

            /*
             * STEP 1
             * Save all permission IDs
             */
            List<Long> permissionIds = appUserRole.getRolePermissions()
                    .stream()
                    .map(rp -> rp.getPermission().getPermissionId())
                    .toList();

            /*
             * STEP 2
             * Delete all role permissions
             */
            appRolePermissionRepository.deleteAll(appUserRole.getRolePermissions());
            appRolePermissionRepository.flush();

            appUserRole.getRolePermissions().clear();

            /*
             * STEP 3
             * Delete all permissions
             */
            if (!permissionIds.isEmpty()) {
                appPermissionRepository.deleteAllById(permissionIds);
                appPermissionRepository.flush();
            }

            /*
             * STEP 4
             * Update role
             */
            appUserRole.setUserRole(request.getRole());

            /*
             * STEP 5
             * Insert new permissions
             */
            List<AppRoleMapping> mappings =
                    appRoleMappingRepository.findByRole(request.getRole().name());

            for (AppRoleMapping mapping : mappings) {

                AppPermission permission = new AppPermission();
                permission.setPermissionCode(mapping.getPermissionCode());
                permission.setDescription(mapping.getDescription());

                permission = appPermissionRepository.save(permission);

                AppRolePermission rolePermission = new AppRolePermission();
                rolePermission.setUserRole(appUserRole);
                rolePermission.setPermission(permission);

                appUserRole.getRolePermissions().add(rolePermission);
            }
        }

        appUserRepository.save(appUser);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("appUser", appUser);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("User account updated successfully!")
                .responseBody(responseBody)
                .build();
    }

    @Transactional
    public CommonResponse softDeleteUserAccount(String uuid, String username) throws ServiceException {

        /* Find user */
        AppUser appUser = appUserRepository.findUserByUsername(username)
                .orElseThrow(() -> new ServiceException(
                        HttpStatus.NOT_FOUND.value(),
                        "User not found: " + username));

        /* Prevent deleting Super Admin */
        boolean isSuperAdmin = appUser.getUserRole()
                .stream()
                .anyMatch(role -> role.getUserRole() == UserRole.SUPER_ADMIN);

        if (isSuperAdmin) {
            loggingService.error(uuid, getClass().getName(),"Super Admin account cannot be disabled.", HttpStatus.FORBIDDEN.value());
            throw new ServiceException(HttpStatus.FORBIDDEN.value(), "Super Admin account cannot be disabled.");
        }

        /* Already disabled */
        if (appUser.getStatus() == UserStatus.DISABLED) {
            return CommonResponse.builder()
                    .returnCode(HttpStatus.OK.value())
                    .returnMessage("User account [" + username + "] is already disabled.")
                    .build();
        }

        /* Soft delete */
        appUser.setStatus(UserStatus.DISABLED);
        appUser.setActive(false);

        appUserRepository.save(appUser);

        return CommonResponse.builder()
                .returnCode(HttpStatus.OK.value())
                .returnMessage("User account " + username + " has been disabled.")
                .build();
    }

    public Map<String, Object> getUserInfo(String username) throws ServiceException {

        /* Find user */
        AppUser appUser = appUserRepository.findUserByUsername(username)
                .orElseThrow(() -> new ServiceException(
                        HttpStatus.NOT_FOUND.value(),
                        "User not found: " + username));

        AppWeeklyTimesheet currentTimesheet = appUser.getAppEmployeeProfile()
                .getTimesheets()
                .stream()
                .max(Comparator.comparing(AppWeeklyTimesheet::getWeekEnding))
                .orElse(null);

        TimesheetStatus status = currentTimesheet != null ? currentTimesheet.getStatus() : TimesheetStatus.OPEN;
        loggingService.info("", this.getClass().getName(), "", "TimesheetStatus : " + status);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("employeeId", appUser.getAppEmployeeProfile().getEmployeeId());
        responseBody.put("employeeName", appUser.getAppEmployeeProfile().getFirstName() + " " + appUser.getAppEmployeeProfile().getLastName());
        responseBody.put("branchAssign", appUser.getAppEmployeeProfile().getEmploymentInformation().getBranchAssign());
        responseBody.put("status", currentTimesheet != null ? currentTimesheet.getStatus() : status);
        return responseBody;
    }
}
