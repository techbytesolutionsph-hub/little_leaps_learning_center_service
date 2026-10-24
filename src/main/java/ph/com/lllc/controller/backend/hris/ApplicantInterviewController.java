package ph.com.lllc.controller.backend.hris;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ph.com.lllc.dto.hris.ApplicantInterviewRequest;
import ph.com.lllc.dto.hris.InterviewNotesRequest;
import ph.com.lllc.dto.hris.InterviewResultRequest;
import ph.com.lllc.dto.hris.RescheduleInterviewRequest;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.service.api.hris.ApplicantInterviewService;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.service.util.uuid.GenerateUUIDService;

@RestController
@RequiredArgsConstructor
@Tag(name = "Applicant Interview Controller")
@RequestMapping("/api/v1/applicant-interview")
public class ApplicantInterviewController {

    private final ApplicantInterviewService applicantInterviewService;
    private final GenerateUUIDService generateUUIDService;
    private final LoggingService loggingService;

    @Operation(summary = "Register Candidate")
    @PostMapping("/register-applicant-interview")
    public ResponseEntity<CommonResponse> createApplicantInterview(@Valid @RequestBody ApplicantInterviewRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "ApplicantInterviewRequest : " + request.toString());
        return ResponseEntity.status(HttpStatus.CREATED).body(applicantInterviewService.createApplicantInterview(uuid, request));
    }

    @Operation(summary = "Update Interview Notes")
    @PutMapping("/update-interview-notes")
    public ResponseEntity<Void> updateInterviewNotes(@Valid @RequestBody InterviewNotesRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "InterviewNotesRequest : " + request.toString());
        applicantInterviewService.updateInterviewNotes(uuid, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Update Interview Result")
    @PutMapping("/update-interview-result")
    public ResponseEntity<Void> updateInterviewResult(@Valid @RequestBody InterviewResultRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "InterviewResultRequest : " + request.toString());
        applicantInterviewService.updateInterviewResult(uuid, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Reschedule Interview")
    @PutMapping("/reschedule-interview")
    public ResponseEntity<Void> rescheduleInterview(@Valid @RequestBody RescheduleInterviewRequest request) throws ServiceException {
        String uuid = generateUUIDService.generateUUID();
        loggingService.info(uuid, this.getClass().getName(), "", "RescheduleInterviewRequest : " + request.toString());
        applicantInterviewService.rescheduleInterview(uuid, request);
        return ResponseEntity.noContent().build();
    }
}
