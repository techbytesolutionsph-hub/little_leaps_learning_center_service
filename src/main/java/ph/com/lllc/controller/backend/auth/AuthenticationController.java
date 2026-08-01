package ph.com.lllc.controller.backend.auth;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.com.lllc.dto.auth.ClientLoginRequest;
import ph.com.lllc.dto.auth.LoginResponse;
import ph.com.lllc.dto.auth.PortalLoginRequest;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.auth.AuthenticationService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Login Controller")
@RequestMapping("/api/v1/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;
    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;

    @Operation(summary = "Client Login")
    @PostMapping("/client/login")
    public ResponseEntity<LoginResponse> clientLogin(
            @Valid @RequestBody ClientLoginRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "ClientLoginRequest : " + request.toString());
        return ResponseEntity.ok(authenticationService.clientLogin(uuid, request, httpRequest));
    }

    @Operation(summary = "Client Logout")
    @PostMapping("/client/logout")
    public ResponseEntity<CommonResponse> clientLogout(
            HttpServletRequest httpRequest) {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "Logout client!");
        return ResponseEntity.ok(authenticationService.logout(uuid, false, httpRequest));
    }

    @Operation(summary = "Portal Login")
    @PostMapping("/portal/login")
    public ResponseEntity<LoginResponse> portalLogin(
            @Valid @RequestBody PortalLoginRequest request,
            HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "StaffLoginRequest : " + request.toString());
        return ResponseEntity.ok(authenticationService.portalLogin(uuid, request, httpRequest));
    }

    @Operation(summary = "Portal Logout")
    @PostMapping("/portal/logout")
    public ResponseEntity<CommonResponse> portalLogout(
            HttpServletRequest httpRequest) {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "Logout staff!");
        return ResponseEntity.ok(authenticationService.logout(uuid, true, httpRequest));
    }
}
