package ph.com.lllc.service.api.hris;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ph.com.lllc.dto.hris.ApplicantInterviewResponse;
import ph.com.lllc.dto.hris.ApplicantRequest;
import ph.com.lllc.dto.hris.ApplicantResponse;
import ph.com.lllc.dto.response.CommonResponse;
import ph.com.lllc.entity.user.hris.AppApplicant;
import ph.com.lllc.entity.user.hris.AppApplicantInterview;
import ph.com.lllc.enums.hris.ApplicantStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.hris.AppApplicantRepository;
import ph.com.lllc.service.util.logging.LoggingService;
import ph.com.lllc.util.LocalDateUtils;
import ph.com.lllc.util.ObjectUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RecruitmentService {

    private final AppApplicantRepository appApplicantRepository;
    private final LoggingService loggingService;

    public CommonResponse createApplicant(String uuid, ApplicantRequest request) {

        AppApplicant applicant = ObjectUtils.copyAs(request, AppApplicant.class);
        applicant.setAppliedDate(request.getAppliedDate() != null
                        ? request.getAppliedDate()
                        : LocalDateUtils.getLocalDate());

        AppApplicant savedApplicant = appApplicantRepository.save(applicant);
        loggingService.info(uuid, this.getClass().getName(), "", "Candidate saved successfully!");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("client", savedApplicant);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Candidate saved successfully!")
                .responseBody(responseBody)
                .build();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<ApplicantResponse> getAllApplicants() {
        List<AppApplicant> applicants = appApplicantRepository.findAll(
                Sort.by(Sort.Order.desc("appliedDate").nullsLast())
        );
        return ObjectUtils.copyListAs(applicants, ApplicantResponse.class);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<ApplicantResponse> getAllAppliedAndScheduledApplicants() {
        List<AppApplicant> applicants = appApplicantRepository.findByAppliedStageIn(
                List.of(ApplicantStatus.APPLIED, ApplicantStatus.SCREENING, ApplicantStatus.INTERVIEW));
        return ObjectUtils.copyListAs(applicants, ApplicantResponse.class);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<ApplicantResponse> getAllJobOfferedApplicants() {
        List<AppApplicant> applicants = appApplicantRepository.findByAppliedStageIn(List.of(ApplicantStatus.OFFERED));
        return ObjectUtils.copyListAs(applicants, ApplicantResponse.class);
    }


    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public List<ApplicantResponse> findByAppliedStage(ApplicantStatus stage) {

        List<AppApplicant> applicants = appApplicantRepository.findByAppliedStage(stage);
        List<ApplicantResponse> responses = new ArrayList<>(ObjectUtils.copyListAs(applicants, ApplicantResponse.class));

        // Latest interview date and time first.
        Comparator<AppApplicantInterview> newestInterviewFirst =
                Comparator.comparing(
                        AppApplicantInterview::getInterviewDate,
                        Comparator.nullsLast(Comparator.reverseOrder())
                ).thenComparing(
                        AppApplicantInterview::getInterviewTime,
                        Comparator.nullsLast(Comparator.reverseOrder())
                );

        for (int i = 0; i < applicants.size(); i++) {

            AppApplicant entity = applicants.get(i);
            ApplicantResponse response = responses.get(i);

            List<ApplicantInterviewResponse> sortedInterviews =
                    entity.getInterviews() == null ? new ArrayList<>()
                            : entity.getInterviews()
                            .stream()
                            .sorted(newestInterviewFirst)
                            .map(interview ->
                                    ObjectUtils.copyAs(interview, ApplicantInterviewResponse.class)
                            )
                            .collect(Collectors.toCollection(ArrayList::new));

            response.setInterviews(sortedInterviews);

            ApplicantInterviewResponse latestInterview = sortedInterviews.stream()
                    .filter(interview -> interview.getInterviewResult() == null)
                    .findFirst()
                    .orElseGet(() -> sortedInterviews.stream()
                            .filter(interview -> interview.getInterviewResult() != null)
                            .max(Comparator.comparing(
                                    ApplicantInterviewResponse::getInterviewDate,
                                    Comparator.nullsFirst(Comparator.naturalOrder())
                            ).thenComparing(
                                    ApplicantInterviewResponse::getInterviewTime,
                                    Comparator.nullsFirst(Comparator.naturalOrder())
                            ))
                            .orElse(null));

            response.setLatestInterview(latestInterview);
        }

        return responses;
    }

    @Transactional
    public CommonResponse updateApplicant(String uuid, ApplicantRequest request) throws ServiceException {

        AppApplicant applicant = this.getApplicant(request.getId());

        applicant.setFirstName(request.getFirstName());
        applicant.setMiddleName(request.getMiddleName());
        applicant.setLastName(request.getLastName());
        applicant.setSuffix(request.getSuffix());
        applicant.setEmail(request.getEmail());
        applicant.setPhoneNumber(request.getPhoneNumber());
        applicant.setAppliedJob(request.getAppliedJob());
        applicant.setLocation(request.getLocation());
        applicant.setAppliedDate(request.getAppliedDate());
        applicant.setExperience(request.getExperience());
        applicant.setNotes(request.getNotes());
        applicant.setResumeFilename(request.getResumeFilename());
        applicant.setResumeLink(request.getResumeLink());

        AppApplicant updateApplicant = appApplicantRepository.save(applicant);
        loggingService.info(uuid, this.getClass().getName(), "", "Candidate updated successfully!");

        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("client", updateApplicant);

        return CommonResponse.builder()
                .returnCode(HttpStatus.CREATED.value())
                .returnMessage("Candidate updated successfully!")
                .responseBody(responseBody)
                .build();
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public AppApplicant getApplicant(Long applicantId) throws ServiceException {
        return appApplicantRepository.findById(applicantId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Applicant not found: " + applicantId));
    }

    public AppApplicant moveToScreening(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setAppliedStage(ApplicantStatus.SCREENING);

        return applicant;
    }

    public AppApplicant moveToInterview(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setAppliedStage(ApplicantStatus.INTERVIEW);
        return applicant;
    }

    public AppApplicant rejectApplicant(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setAppliedStage(ApplicantStatus.REJECTED);

        return applicant;
    }

    public AppApplicant withdrawApplicant(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setAppliedStage(ApplicantStatus.WITHDRAWN);

        return applicant;
    }

    public AppApplicant updateApplicant(ApplicantRequest request) throws ServiceException {

        AppApplicant applicant = appApplicantRepository.findById(request.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Applicant not found: " + request.getId()));

        applicant.setFirstName(request.getFirstName());
        applicant.setMiddleName(request.getMiddleName());
        applicant.setLastName(request.getLastName());
        applicant.setSuffix(request.getSuffix());
        applicant.setEmail(request.getEmail());
        applicant.setPhoneNumber(request.getPhoneNumber());

        if (request.getAppliedDate() != null) {
            applicant.setAppliedDate(request.getAppliedDate());
        }

        applicant.setAppliedStage(request.getAppliedStage());

        return appApplicantRepository.save(applicant);
    }

    /* KPIs Section */
    public long countByAppliedDateBetween(LocalDate startDate, LocalDate endDate){
        return appApplicantRepository.countByAppliedDateBetween(startDate, endDate);
    }

    public long countAllByApplicantStatus(ApplicantStatus status){
        return appApplicantRepository.findByAppliedStage(status).size();
    }

    public long countByApplicantStatusAndAppliedDateBetween(ApplicantStatus status, LocalDate startDate, LocalDate endDate){
        return appApplicantRepository.countByAppliedStageAndAppliedDateBetween(
                status,
                startDate,
                endDate
        );
    }
}
