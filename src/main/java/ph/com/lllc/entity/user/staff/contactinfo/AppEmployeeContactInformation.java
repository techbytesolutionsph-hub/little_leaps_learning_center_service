package ph.com.lllc.entity.user.staff.contactinfo;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_employee_contact_information")
@NoArgsConstructor
@AllArgsConstructor
public class AppEmployeeContactInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contact_number")
    private String contactNumber;

    @Column(name = "work_email")
    private String workEmail;

    @Column(name = "home_email")
    private String homeEmail;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_profile_id", nullable = false, unique = true)
//    @JsonIgnoreProperties("contactInformation")
    private AppEmployeeProfile appEmployeeProfile;
}