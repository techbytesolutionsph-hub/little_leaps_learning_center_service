package ph.com.lllc.controller.backend.clients;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.clients.AssignClientRequest;
import ph.com.lllc.dto.staff.clients.AssignedClientResponse;
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

    @Operation(summary = "Register Client")
    @PostMapping("/register-client")
    public ResponseEntity<CommonResponse> registerClient(@Valid @RequestBody ClientRegistrationRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "ClientRegistrationRequest : " + request.toString());
        return ResponseEntity.ok(clientManagementService.registerClient(uuid, request));
    }

    @Operation(summary = "Get Clients")
    @GetMapping("/get-clients")
    public ResponseEntity<List<ClientRegistrationResponse>> getClientProfiles(){
        return new ResponseEntity<>(clientManagementService.getClientProfiles(), HttpStatus.OK);
    }

    @Operation(summary = "Get Client by Client ID")
    @GetMapping("/get-client/{clientId}")
    public ResponseEntity<ClientRegistrationResponse> getClientProfiles(@PathVariable String clientId) throws ServiceException {
        return new ResponseEntity<>(clientManagementService.getClientProfileByClientId(clientId), HttpStatus.OK);
    }

    @Operation(summary = "Update Client")
    @PutMapping("/update-client")
    public ResponseEntity<CommonResponse> updateClient(@Valid @RequestBody ClientRegistrationRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "ClientRegistrationRequest : " + request.toString());
        return ResponseEntity.ok(clientManagementService.updateClient(uuid, request));
    }

    @Operation(summary = "Assign Client")
    @PostMapping("/assign-client")
    public ResponseEntity<CommonResponse> assignClient(@Valid @RequestBody AssignClientRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "AssignClientRequest : " + request.toString());
        return ResponseEntity.ok(clientManagementService.assignClient(uuid, request, httpRequest));
    }

    @Operation(summary = "Get Assign Clients")
    @GetMapping("/get-assign-clients")
    public ResponseEntity<List<AssignedClientResponse>> getAssignedClients() {
        return ResponseEntity.ok(clientManagementService.getAssignedClients());
    }

    @Operation(summary = "Get Assign Client")
    @GetMapping("/get-assign-client")
    public ResponseEntity<AssignedClientResponse> getAssignedClient(@RequestParam("id") String assignmentId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "Assignment ID : " + assignmentId);
        return ResponseEntity.ok(clientManagementService.getAssignedClient(uuid, assignmentId));
    }
}
