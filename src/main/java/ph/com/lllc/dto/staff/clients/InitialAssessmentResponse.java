package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InitialAssessmentResponse {

    private String initialAssessmentId;
    private LocalDate assessmentDate;
    private ScheduleStatus scheduleStatus;
    private String notes;

    private List<InitialAssessmentDto> slots;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InitialAssessmentDto {
        private LocalTime startTime;
        private LocalTime endTime;
    }

    /* Client School ID */
    private String clientStudentId;

    /* Client Information */
    private String clientId;
    private String clientProfilePicture;
    private String clientFullName;
    private LocalDate clientBirthDate;
    private Integer clientAge;
    private Gender clientGender;

    /* Client Enrollment Details */
    private LocalDate dateEnrolled;
    private Set<DiagnosisConcern> diagnosisConcerns;
    private String programType;
    private AssignmentStatus assignmentStatus;
    private String branch;
    private EnrollmentStatus enrollmentStatus;

    private String guardianFullName;
    private String guardianEmail;
    private String guardianContactNo;

    private String employeeId;
    private String assigneeProfilePicture;
    private String assigneeFullName;
    private String assigneePosition;
    private LocalDate assignedAt;
    private AssignmentRole assignmentRole;

    List<AssignmentHistoryResponse> history;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentHistoryResponse{

        private String description;
        private AssignmentHistoryAction action;
        private String assigneeFullName;
        private AssignmentRole assignmentRole;
        private AssignmentStatus assignmentStatus;
        private String assignedByFullName;
        private LocalDateTime eventDateTime;
    }
}
