package ph.com.lllc.entity.user.staff.address;

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
@Table(name = "lllc_app_employee_address")
@NoArgsConstructor
@AllArgsConstructor
public class AppEmployeeAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "street")
    private String street;

    @Column(name = "barangay")
    private String barangay;

    @Column(name = "city")
    private String city;

    @Column(name = "province")
    private String province;

    @Column(name = "country")
    private String country;

    @Column(name = "zip_no")
    private String zipNumber;

    @Column(name = "is_permanent_residence")
    private boolean isPermanentResidence;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_profile_id", nullable = false)
//    @JsonIgnoreProperties("address")
    private AppEmployeeProfile appEmployeeProfile;
}
