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

    private String clientFullName;
    private LocalDate clientBirthDate;
    private Integer clientAge;
    private Gender clientGender;
    private LocalDate dateEnrolled;

    private String guardianFullName;
    private String guardianEmail;
    private String guardianContactNo;

    private Integer currentAssignmentCount;
    private Integer currentActiveCount;
    private Integer currentEndedCount;

    private String caseManagerFullName;
    private String caseManagerPosition;

    private Set<DiagnosisConcern> diagnosisConcerns;
    private String programType;
    private AssignmentStatus status;
    private String branch;
    private String notes;
    private LocalDate assignedAt;
    private LocalDate unassignedAt;

    private String employeeId;
    private String assigneeFullName;
    private String assigneePosition;
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