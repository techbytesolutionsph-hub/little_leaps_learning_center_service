package ph.com.lllc.dto.response;

import lombok.*;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class DashboardMetricsResponse {

    private DashboardCardResponse totalEmployees;
    private DashboardCardResponse totalActiveEmployees;
    private DashboardCardResponse totalOnLeaveEmployees;
    private DashboardCardResponse totalResignedEmployees;

}
