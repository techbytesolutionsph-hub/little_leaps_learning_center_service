package ph.com.lllc.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class ClientLoginResponse implements LoginResponse {

    private boolean success;
    private String message;
    private Object data;
    private LocalDateTime timestamp;
}
