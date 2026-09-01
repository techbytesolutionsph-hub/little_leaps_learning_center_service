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
public class AssignClientRequest {

    private String assignmentId;
    private String clientId;
    private String caseManagerId;
    private AssignmentRole caseManagerRole;
    private String behavioralTherapistId;
    private AssignmentRole behavioralTherapistRole;
    private AssignmentStatus assignStatus;
    private LocalDate assignedDate;
    private String notes;
}
