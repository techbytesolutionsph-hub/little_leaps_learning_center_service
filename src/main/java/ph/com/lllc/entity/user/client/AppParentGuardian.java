package ph.com.lllc.entity.user.client;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.enums.Gender;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_parent_guardian")
@NoArgsConstructor
@AllArgsConstructor
public class AppParentGuardian implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_parent_id")
    private Long id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "relation_to_client")
    private String relationshipToClient;

    @Column(name = "address")
    private String address;

    @Column(name = "email")
    private String email;

    @JsonBackReference("client-parent")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id", nullable = false)
    private AppClientProfile appClientProfile;

}
