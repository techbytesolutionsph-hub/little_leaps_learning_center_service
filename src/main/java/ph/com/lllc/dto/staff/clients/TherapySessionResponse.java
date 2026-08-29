package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TherapySessionResponse {

    private String therapySessionId;
    private AssignmentRole assignmentRole;
    private SessionFrequency frequency;
    private ScheduleStatus status;
    private String notes;

    private List<TherapySlotRequest> scheduleSlots;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TherapySlotRequest {
        private LocalDate therapyDate;
        private DayOfWeek day;
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

    private String caseManagerId;
    private String caseManagerProfilePicture;
    private String caseManagerFullName;
    private String caseManagerPosition;
    private LocalDate caseManagerAssignedAt;

    private String therapistId;
    private String therapistProfilePicture;
    private String therapistFullName;
    private String therapistPosition;

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
