package ph.com.lllc.service.api.hris;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ph.com.lllc.dto.hris.AppApplicantRequest;
import ph.com.lllc.entity.user.hris.AppApplicant;
import ph.com.lllc.enums.hris.ApplicantStatus;
import ph.com.lllc.exception.ServiceException;
import ph.com.lllc.repository.hris.AppApplicantRepository;
import ph.com.lllc.util.LocalDateUtils;
import ph.com.lllc.util.ObjectUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class AppApplicantService {

    private final AppApplicantRepository appApplicantRepository;

    public AppApplicant createApplicant(AppApplicantRequest request) {

        AppApplicant applicant = ObjectUtils.copyAs(request, AppApplicant.class);
        applicant.setDateOfApplication(request.getDateOfApplication() != null
                        ? request.getDateOfApplication()
                        : LocalDateUtils.getLocalDate());

        return appApplicantRepository.save(applicant);
    }

    @Transactional(propagation = Propagation.NOT_SUPPORTED, readOnly = true)
    public AppApplicant getApplicant(Long applicantId) throws ServiceException {
        return appApplicantRepository.findById(applicantId)
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Applicant not found: " + applicantId));
    }

    public AppApplicant moveToScreening(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setStatus(ApplicantStatus.SCREENING);

        return applicant;
    }

    public AppApplicant moveToInterview(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setStatus(ApplicantStatus.INTERVIEW);
        return applicant;
    }

    public AppApplicant rejectApplicant(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setStatus(ApplicantStatus.REJECTED);

        return applicant;
    }

    public AppApplicant withdrawApplicant(Long applicantId) throws ServiceException {
        AppApplicant applicant = this.getApplicant(applicantId);
        applicant.setStatus(ApplicantStatus.WITHDRAWN);

        return applicant;
    }

    public AppApplicant updateApplicant(AppApplicantRequest request) throws ServiceException {

        AppApplicant applicant = appApplicantRepository.findById(request.getId())
                .orElseThrow(() -> new ServiceException(HttpStatus.NOT_FOUND.value(), "Applicant not found: " + request.getId()));

        applicant.setFirstName(request.getFirstName());
        applicant.setMiddleName(request.getMiddleName());
        applicant.setLastName(request.getLastName());
        applicant.setSuffix(request.getSuffix());
        applicant.setEmail(request.getEmail());
        applicant.setPhoneNumber(request.getPhoneNumber());

        if (request.getDateOfApplication() != null) {
            applicant.setDateOfApplication(request.getDateOfApplication());
        }

        applicant.setStatus(request.getStatus());

        return appApplicantRepository.save(applicant);
    }
}
