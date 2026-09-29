package ph.com.lllc.controller.backend.management;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.dto.staff.management.LeaveRequest;
import ph.com.lllc.enums.LeaveType;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.management.LeaveRequestService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Leave Request Controller")
@RequestMapping("/api/v1/leave-request")
public class LeaveRequestController {

    private final LeaveRequestService leaveRequestService;
    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;

    @PostMapping("/save-leave-request")
    @Operation(summary = "Save Leave Request")
    public ResponseEntity<CommonResponse> saveLeaveRequest(@RequestBody LeaveRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "LeaveRequest : " + request.toString());
        return new ResponseEntity<>(leaveRequestService.saveLeaveRequest(uuid, request), HttpStatus.CREATED);
    }

    @PostMapping("/available-leave-balance")
    @Operation(summary = "Get Available Leave Balance")
    public ResponseEntity<Double> getAvailableLeaveBalance(@RequestParam String employeeId, @RequestParam LeaveType leaveType) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "Employee ID : " + employeeId + " Leave Type : " + leaveType.name());
        return ResponseEntity.ok(leaveRequestService.getAvailableLeaveBalance(uuid, employeeId, leaveType));
    }
}
