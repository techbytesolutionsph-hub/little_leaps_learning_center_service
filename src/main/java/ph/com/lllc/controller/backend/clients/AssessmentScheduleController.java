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
import ph.com.lllc.dto.staff.clients.InitialAssessmentRequest;
import ph.com.lllc.dto.staff.clients.InitialAssessmentResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.clients.AssessmentScheduleService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Client Assessment Schedule Controller")
@RequestMapping("/api/v1/assessment")
public class AssessmentScheduleController {

    private final LoggingService loggingService;
    private final GenerateUUIDService generateUUIDService;
    private final AssessmentScheduleService assessmentScheduleService;

    @Operation(summary = "Save Initial Assessment Schedule")
    @PostMapping("/save-initial-assessment")
    public ResponseEntity<CommonResponse> registerClient(@Valid @RequestBody InitialAssessmentRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "InitialAssessmentRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveInitialAssessmentSchedule(uuid, request, httpRequest));
    }

    @Operation(summary = "Get Initial Assessments")
    @GetMapping("/get-initial-assessments")
    public ResponseEntity<List<InitialAssessmentResponse>> getAssignedClients() {
        return ResponseEntity.ok(assessmentScheduleService.getInitialAssessmentSchedule());
    }
}
