package ph.com.lllc.controller.backend.hris;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ph.com.lllc.dto.hris.ApplicantInterviewRequest;
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
}
