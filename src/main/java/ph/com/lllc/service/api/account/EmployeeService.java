package ph.com.lllc.service.api.account;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ph.com.lllc.dto.account.CreateEmployeeRequest;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.entity.user.common.*;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.UserStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.AppPermissionRepository;
import ph.com.lllc.repository.AppRoleMappingRepository;
import ph.com.lllc.repository.AppUserRepository;
import ph.com.lllc.service.db.SequenceGeneratorService;
import ph.com.lllc.service.util.IdGeneratorUtils;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.util.BCryptUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class EmployeeService {

    private final AppUserRepository appUserRepository;
    private final AppRoleMappingRepository appRoleMappingRepository;
    private final AppPermissionRepository appPermissionRepository;
    private final SequenceGeneratorService sequenceGeneratorService;
    private final IdGeneratorUtils idGeneratorUtils;
    private final LoggingService loggingService;
    private final BCryptUtils encoder;

    @Transactional
    public CommonResponse createEmployeeAccount(String uuid, CreateEmployeeRequest request) throws ServiceException {

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
        appUser.setStatus(UserStatus.ACTIVE);
        appUser.setActive(true);

        long nextUserSeq = sequenceGeneratorService.getStaffIdNextSequence();
        String employeeId = idGeneratorUtils.generateEmployeeId(request.getRole().getCode(), nextUserSeq);

        /* Create Staff Profile */
        AppEmployeeProfile staffProfile = this.getAppStaffProfile(request, employeeId, appUser);
        appUser.setAppEmployeeProfile(staffProfile);

        /* Create User Role */
        AppUserRole userRole = new AppUserRole();
        userRole.setUserRole(request.getRole());
        userRole.setAppUser(appUser);
        appUser.setUserRole(List.of(userRole));

        /*
         * Get permissions by role
         */
        List<AppRoleMapping> roleMappings = appRoleMappingRepository.findByRole(request.getRole().name());

        List<AppRolePermission> rolePermissions = new ArrayList<>();

        for (AppRoleMapping mapping : roleMappings) {

            AppPermission permission = appPermissionRepository
                    .findByPermissionCode(mapping.getPermissionCode())
                    .orElseGet(() -> {
                        AppPermission newPermission = new AppPermission();
                        newPermission.setPermissionCode(mapping.getPermissionCode());
                        newPermission.setDescription(mapping.getDescription());
                        return appPermissionRepository.save(newPermission);
                    });

            AppRolePermission rolePermission = new AppRolePermission();
            rolePermission.setUserRole(userRole);
            rolePermission.setPermission(permission);
            rolePermissions.add(rolePermission);
        }

        userRole.setRolePermissions(rolePermissions);

        appUserRepository.save(appUser);

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("appStaffProfile", staffProfile);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Employee account created successfully!")
                .responseBody(responseBody)
                .build();
    }

    private AppEmployeeProfile getAppStaffProfile(CreateEmployeeRequest request, String employeeId, AppUser appUser) {
        AppEmployeeProfile staffProfile = new AppEmployeeProfile();
        staffProfile.setEmployeeId(employeeId);
        staffProfile.setFirstName(request.getFirstName());
        staffProfile.setMiddleName(request.getMiddleName());
        staffProfile.setLastName(request.getLastName());
        staffProfile.setEmail(request.getEmail());
        staffProfile.setDateOfBirth(request.getDateOfBirth());
        staffProfile.setGender(request.getGender());
        staffProfile.setPhoneNumber(request.getPhoneNumber());
        staffProfile.setStaffType(request.getStaffType());
        staffProfile.setProfileImageUrl(request.getProfileImageUrl());
        staffProfile.setAppUser(appUser);
        return staffProfile;
    }
}
