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
import ph.com.lllc.enums.UserStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.AppPermissionRepository;
import ph.com.lllc.repository.AppRoleMappingRepository;
import ph.com.lllc.repository.AppUserRepository;
import ph.com.lllc.service.util.PasswordGeneratorService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.util.BCryptUtils;
import ph.com.lllc.util.ObjectUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class UserAccountService {

    private final AppUserRepository appUserRepository;
    private final AppRoleMappingRepository appRoleMappingRepository;
    private final AppPermissionRepository appPermissionRepository;
    private final PasswordGeneratorService passwordGeneratorService;
    private final LoggingService loggingService;
    private final BCryptUtils encoder;

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
        appUser.setStatus(UserStatus.ACTIVE);
        appUser.setProfileImageUrl(request.getProfileImageUrl());
        appUser.setActive(true);

        AppUserRole appUserRole = AppUserRole.builder()
                .userRole(request.getRole())
                .appUser(appUser)
                .build();

        appUser.setUserRole(List.of(appUserRole));

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
            rolePermission.setUserRole(appUserRole);
            rolePermission.setPermission(permission);
            rolePermissions.add(rolePermission);
        }

        appUserRole.setRolePermissions(rolePermissions);

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

    public AppUserResponse findByUsername(String username){
        AppUser user = appUserRepository.findByUsername(username);

        AppUserResponse response = ObjectUtils.copyAs(user, AppUserResponse.class);

        if (user.getUserRole() != null && !user.getUserRole().isEmpty()) {
            response.setRole(user.getUserRole().get(0).getUserRole());
        }

        return response;
    }

}
