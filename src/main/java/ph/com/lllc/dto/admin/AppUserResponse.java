package ph.com.lllc.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.enums.UserRole;
import ph.com.lllc.enums.UserStatus;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppUserResponse {

    private Long appUserId;
    private String username;
    private String email;
    private String profileImageUrl;
    private UserRole role;
    private UserStatus status;
    private boolean active;
    private LocalDateTime lastLogin;
    private LocalDateTime creationDate;
    private String createdBy;
    private LocalDateTime lastModificationDate;
    private String lastModifiedBy;
}