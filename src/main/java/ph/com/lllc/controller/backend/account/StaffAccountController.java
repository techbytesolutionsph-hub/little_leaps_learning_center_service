package ph.com.lllc.controller.backend.account;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.com.lllc.dto.account.RegisterStaffRequest;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.account.StaffAccountService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Staff Account Controller")
@RequestMapping("/api/v1/account/staff")
public class StaffAccountController {

    private final StaffAccountService staffAccountService;
    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;

    @Operation(summary = "Create Staff Account")
    @PostMapping("/register")
    public ResponseEntity<CommonResponse> registerStaffAccount(
            @Valid @RequestBody RegisterStaffRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "RegisterStaffRequest : " + request.toString());
        return ResponseEntity.ok(staffAccountService.registerStaffAccount(uuid, request));
    }
}
