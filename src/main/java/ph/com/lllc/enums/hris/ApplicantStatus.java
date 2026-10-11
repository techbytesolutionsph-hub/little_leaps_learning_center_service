package ph.com.lllc.enums.hris;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ApplicantStatus {

    APPLIED("Applied"),
    SCREENING("Screening"),
    INTERVIEW("Interview"),
    OFFERED("Offered"),
    FOR_REQUIREMENTS("For Requirements"),
    REQUIREMENTS_COMPLETED("Requirements Completed"),
    HIRED("Hired"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    private final String stage;
}