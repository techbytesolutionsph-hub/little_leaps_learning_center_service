package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientDetailsResponse {

    private String clientId;
    private String clientFullName;
}
