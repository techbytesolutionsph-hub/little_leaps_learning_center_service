package ph.com.lllc.entity.user.staff.payrollinfo;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.user.staff.generalinfo.AppEmployeeProfile;
import ph.com.lllc.enums.PayrollCycle;
import ph.com.lllc.enums.SalaryType;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_payroll_information")
@NoArgsConstructor
@AllArgsConstructor
public class AppPayrollInformation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    /* Salary Information */
    @Column(name = "basic_salary", precision = 12, scale = 2)
    private BigDecimal basicSalary;

    @Column(name = "daily_rate", precision = 12, scale = 2)
    private BigDecimal dailyRate;

    @Column(name = "hourly_rate", precision = 12, scale = 2)
    private BigDecimal hourlyRate;

    @Enumerated(EnumType.STRING)
    @Column(name = "salary_type")
    private SalaryType salaryType;

    /* Payroll Schedule */
    @Column(name = "payroll_group")
    private String payrollGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "payroll_cycle")
    private PayrollCycle payrollCycle;

    /* Bank Information */
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "bank_account_name")
    private String bankAccountName;

    @Column(name = "bank_account_number")
    private String bankAccountNumber;

    /* Payroll Settings */
    @Column(name = "is_taxable")
    private Boolean taxable = true;

    @Column(name = "overtime_eligible")
    private Boolean overtimeEligible = true;

    @Column(name = "holiday_pay_eligible")
    private Boolean holidayPayEligible = true;

    @Column(name = "thirteenth_month_eligible")
    private Boolean thirteenthMonthEligible = true;

    @Column(name = "effective_date")
    private LocalDate effectiveDate;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_profile_id", unique = true, nullable = false)
    private AppEmployeeProfile appEmployeeProfile;
}
