package ph.com.lllc.controller.backend.management;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.com.lllc.dto.staff.management.WeeklyEndingRequest;
import ph.com.lllc.dto.staff.management.WeeklyTimesheetRequest;
import ph.com.lllc.dto.staff.management.WeeklyTimesheetResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.management.TimesheetService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Employee Timesheet Controller")
@RequestMapping("/api/v1/timesheet")
public class TimesheetController {

    private final TimesheetService timesheetService;
    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;

    @PostMapping("/save-submit-timesheet")
    public ResponseEntity<?> saveTimesheet(@RequestBody WeeklyTimesheetRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "WeeklyTimesheetRequest : " + request);
        return ResponseEntity.ok(timesheetService.saveTimesheet(uuid, request));
    }

    @PostMapping("/get-timesheet")
    public ResponseEntity<WeeklyTimesheetResponse> getTimesheet(@RequestBody WeeklyEndingRequest request) {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "WeeklyEndingRequest : " + request);
        return ResponseEntity.ok(timesheetService.getTimesheet(uuid, request));
    }
}
