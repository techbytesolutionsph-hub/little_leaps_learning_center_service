package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedClientResponse {

    private Long id;
    private String assignmentId;

    private String clientId;
    private String clientProfilePicture;
    private String clientFullName;
    private LocalDate clientBirthDate;
    private Integer clientAge;
    private Gender clientGender;
    private LocalDate dateEnrolled;

    private Set<DiagnosisConcern> diagnosisConcerns;
    private String programType;
    private AssignmentStatus status;
    private String branch;
    private String notes;
    private LocalDate assignedAt;
    private LocalDate unassignedAt;

    private String guardianFullName;
    private String guardianEmail;
    private String guardianContactNo;

    private Integer currentAssignmentCount;
    private Integer currentActiveCount;
    private Integer currentEndedCount;

    private String caseManagerId;
    private String caseManagerProfilePicture;
    private String caseManagerFullName;
    private String caseManagerPosition;
    private AssignmentRole caseManagerRole;

    private String behavioralTherapistId;
    private String behavioralTherapistProfilePicture;
    private String behavioralTherapistFullName;
    private String behavioralTherapistPosition;
    private AssignmentRole behavioralTherapistRole;

    List<AssignmentHistoryResponse> history;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AssignmentHistoryResponse{

        private String description;
        private AssignmentHistoryAction action;

        private String caseManagerFullName;
        private AssignmentRole caseManagerRole;

        private String behavioralTherapistFullName;
        private AssignmentRole behavioralTherapistRole;

        private AssignmentStatus assignmentStatus;
        private String assignedByFullName;
        private LocalDateTime eventDateTime;
    }
}