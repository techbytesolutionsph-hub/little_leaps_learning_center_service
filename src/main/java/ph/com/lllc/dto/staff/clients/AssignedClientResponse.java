package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;
import ph.com.lllc.enums.DiagnosisConcern;

import java.time.LocalDate;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignedClientResponse {

    private Long id;
    private String clientId;
    private String clientName;
    private String employeeId;
    private String employeeName;
    private AssignmentRole assignmentRole;
    private Set<DiagnosisConcern> diagnosisConcerns;
    private AssignmentStatus status;
    private String branch;
    private String notes;
    private LocalDate assignedAt;
    private LocalDate unassignedAt;
}