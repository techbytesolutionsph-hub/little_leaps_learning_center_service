package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.com.lllc.enums.AssignmentHistoryAction;
import ph.com.lllc.enums.AssignmentRole;
import ph.com.lllc.enums.AssignmentStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentHistoryResponse {

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
