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
import ph.com.lllc.dto.staff.clients.*;
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
    public ResponseEntity<CommonResponse> saveInitialAssessmentSchedule(@Valid @RequestBody InitialAssessmentRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "InitialAssessmentRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveInitialAssessmentSchedule(uuid, request, httpRequest));
    }

    @Operation(summary = "Get Initial Assessments")
    @GetMapping("/get-initial-assessments")
    public ResponseEntity<List<InitialAssessmentResponse>> getAssignedClients() {
        return ResponseEntity.ok(assessmentScheduleService.getInitialAssessmentSchedules());
    }

    @Operation(summary = "Get Initial Assessment")
    @GetMapping("/get-initial-assessment")
    public ResponseEntity<InitialAssessmentResponse> getAssignedClient(@RequestParam("id") String initialAssessmentId) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "initialAssessmentId : " + initialAssessmentId);
        return ResponseEntity.ok(assessmentScheduleService.getInitialAssessmentSchedule(uuid, initialAssessmentId));
    }

    @Operation(summary = "Update Initial Assessment Schedule")
    @PutMapping("/update-initial-assessment")
    public ResponseEntity<CommonResponse> updateInitialAssessmentSchedule(@Valid @RequestBody InitialAssessmentRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "InitialAssessmentRequest : " + request.toString());
        return ResponseEntity.ok(assessmentScheduleService.updateInitialAssessmentSchedule(uuid, request, httpRequest));
    }

    @Operation(summary = "Save Therapy Session Details")
    @PostMapping("/save-therapy-session-details")
    public ResponseEntity<CommonResponse> saveTherapySessionDetails(@Valid @RequestBody TherapySessionRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "TherapySessionRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveTherapySessionDetails(uuid, request, httpRequest));
    }

    @Operation(summary = "Get All Therapy Session Details")
    @GetMapping("/get-therapy-session-details")
    public ResponseEntity<List<TherapySessionResponse>> getTherapySessionDetails() {
        return ResponseEntity.ok(assessmentScheduleService.getTherapySessionResponse());
    }

    @Operation(summary = "Add Therapy Slot")
    @PostMapping("/save-therapy-slot")
    public ResponseEntity<CommonResponse> saveTherapySlot(@Valid @RequestBody TherapySlotRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "TherapySessionRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveTherapySlot(uuid, request));
    }

    @Operation(summary = "Update Therapy Slot")
    @PutMapping("/update-therapy-slot")
    public ResponseEntity<CommonResponse> updateTherapySlot(@Valid @RequestBody TherapySlotRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "TherapySessionRequest : " + request.toString());
        return ResponseEntity.ok(assessmentScheduleService.updateTherapySlot(uuid, request));
    }

    @Operation(summary = "Save Upgrading Program Details")
    @PostMapping("/save-upgrading-program-details")
    public ResponseEntity<CommonResponse> saveUpgradingProgramDetails(@Valid @RequestBody UpgradingProgramRequest request, HttpServletRequest httpRequest) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "UpgradingProgramRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveUpgradingProgramDetails(uuid, request, httpRequest));
    }

    @Operation(summary = "Add Upgrading Program Slot")
    @PostMapping("/save-upgrading-program-slot")
    public ResponseEntity<CommonResponse> saveUpgradingProgramSlot(@Valid @RequestBody UpgradingProgramSlotRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "UpgradingProgramSlotRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveUpgradingProgramSlot(uuid, request));
    }

    @Operation(summary = "Update Upgrading Program Slot")
    @PutMapping("/update-upgrading-program-slot")
    public ResponseEntity<CommonResponse> updateUpgradingProgramSlot(@Valid @RequestBody UpgradingProgramSlotRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "UpgradingProgramSlotRequest : " + request.toString());
        return ResponseEntity.ok(assessmentScheduleService.updateUpgradingProgramSlot(uuid, request));
    }

    @Operation(summary = "Save Neurodev Assessment Schedule")
    @PostMapping("/save-neurodev-assessment")
    public ResponseEntity<CommonResponse> saveNeurodevAssessment(@Valid @RequestBody NeurodevAssessmentRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "NeurodevAssessmentRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(assessmentScheduleService.saveNeurodevAssessment(uuid, request));
    }

    @Operation(summary = "Get Neurodev Assessments")
    @GetMapping("/get-neurodev-assessments")
    public ResponseEntity<List<NeurodevAssessmentResponse>> getNeurodevAssessments() throws ServiceException {
        return ResponseEntity.ok(assessmentScheduleService.getNeurodevAssessments());
    }

    @Operation(summary = "Get Neurodev Assessment")
    @GetMapping("/get-neurodev-assessment")
    public ResponseEntity<NeurodevAssessmentResponse> getNeurodevAssessment(@RequestParam("id") Long id) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "Neurodev Assessment ID : " + id);
        return ResponseEntity.ok(assessmentScheduleService.getNeurodevAssessments(uuid,id));
    }
}
