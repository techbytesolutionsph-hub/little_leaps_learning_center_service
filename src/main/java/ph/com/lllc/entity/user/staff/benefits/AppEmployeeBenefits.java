package ph.com.lllc.entity.user.staff.benefits;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;

import java.math.BigDecimal;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_employee_benefits")
@NoArgsConstructor
@AllArgsConstructor
public class AppEmployeeBenefits {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /* ==========================
       GOVERNMENT BENEFITS
       ========================== */

    @Column(name = "tin_number", length = 20)
    private String tinNumber;

    @Column(name = "sss_number", length = 20)
    private String sssNumber;

    @Column(name = "philhealth_number", length = 20)
    private String philHealthNumber;

    @Column(name = "pagibig_number", length = 20)
    private String pagIbigNumber;

    /* ==========================
       ALLOWANCES
       ========================== */
    @Column(name = "allowance")
    private BigDecimal allowance;

    @Column(name = "rice_allowance")
    private BigDecimal riceAllowance;

    @Column(name = "transportation_allowance")
    private BigDecimal transportationAllowance;

    @Column(name = "communication_allowance")
    private BigDecimal communicationAllowance;

    /* ==========================
       LEAVES
       ========================== */
    @Column(name = "sick_leave")
    private BigDecimal sickLeave;

    @Column(name = "vacation_leave")
    private BigDecimal vacationLeave;

    @Column(name = "paternity_leave")
    private BigDecimal paternityLeave;

    @Column(name = "maternity_leave")
    private BigDecimal maternityLeave;

    /* ==========================
       TAX
       ========================== */

    @Column(name = "tax_exempt")
    private Boolean taxExempt = false;

    @Column(name = "withholding_tax")
    private BigDecimal withholdingTax;

    /* ==========================
       CONTRIBUTION FLAGS
       ========================== */

    @Column(name = "sss_active")
    private Boolean sssActive = true;

    @Column(name = "philhealth_active")
    private Boolean philHealthActive = true;

    @Column(name = "pagibig_active")
    private Boolean pagIbigActive = true;

    /* ==========================
       COMPANY BENEFITS
       ========================== */

    @Column(name = "has_hmo")
    private Boolean hasHmo = false;

    @Column(name = "hmo_provider")
    private String hmoProvider;

    @Column(name = "hmo_card_number")
    private String hmoCardNumber;

    @Column(name = "life_insurance")
    private Boolean lifeInsurance = false;

    @Column(name = "retirement_plan")
    private Boolean retirementPlan = false;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_profile_id", unique = true, nullable = false)
    private AppEmployeeProfile appEmployeeProfile;
}
