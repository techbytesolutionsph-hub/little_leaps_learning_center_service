package ph.com.lllc.controller.backend.clients;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.ClientRegistrationRequest;
import ph.com.lllc.dto.staff.clients.ClientRegistrationResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.clients.ClientManagementService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Client Management Controller")
@RequestMapping("/api/v1/client")
public class ClientManagementController {

    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;
    private final ClientManagementService clientManagementService;

    @Operation(summary = "Client Logout")
    @PostMapping("/register-client")
    public ResponseEntity<CommonResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "ClientRegistrationRequest : " + request.toString());
        return ResponseEntity.ok(clientManagementService.registerClient(uuid, request));
    }

    @GetMapping("/get-clients")
    @Operation(summary = "Get Clients")
    public ResponseEntity<List<ClientRegistrationResponse>> getClientProfiles(){
        return new ResponseEntity<>(clientManagementService.getClientProfiles(), HttpStatus.OK);
    }
}
