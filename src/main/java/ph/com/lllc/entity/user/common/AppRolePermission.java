package ph.com.lllc.entity.user.common;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name="lllc_app_role_permission")
@NoArgsConstructor
@AllArgsConstructor
public class AppRolePermission implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_permission_id")
    private Long id;

    /*
     * PERMISSION
     */
    @JsonBackReference("permission-role")
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name="permission_id", nullable=false)
    private AppPermission permission;

    /*
     * ROLE
     */
    @JsonBackReference("role-permission")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="role_id", nullable=false)
    private AppUserRole userRole;

}
