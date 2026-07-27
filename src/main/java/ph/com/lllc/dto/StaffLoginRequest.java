package ph.com.lllc.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class StaffLoginRequest {

    @Schema(example = "superuser")
    @NotBlank(message = "Username is required")
    private String username;

    @Schema(example = "P@ssw0rd!")
    @NotBlank(message = "Password is required")
    private String password;
}
