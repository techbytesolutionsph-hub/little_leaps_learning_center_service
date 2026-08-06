package ph.com.lllc.dto.staff;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ph.com.lllc.enums.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {

    private PersonalInformationDTO personalInformation;
    private AddressDTO address;
    private ContactInformationDTO contactInformation;
    private EmergencyContactDTO emergencyContact;
    private EmploymentInformationDTO employmentInformation;
    private BenefitsDTO benefits;
    private PayrollInformationDTO payrollInformation;
    private AccountAccessDTO accountAccess;

    private String profileImageUrl;


    @Data
    public static class PersonalInformationDTO {
        private String firstName;
        private String middleName;
        private String lastName;
        private Integer age;
        private String birthDate;
        private Gender gender;
        private String email;
        private String phoneNumber;
        private MaritalStatus maritalStatus;
    }


    @Data
    public static class AddressDTO {
        private String street;
        private String barangay;
        private String city;
        private String province;
        private String country;
        private String postalCode;
    }


    @Data
    public static class ContactInformationDTO {
        private String contactNumber;
        private String workEmail;
        private String homeEmail;
    }


    @Data
    public static class EmergencyContactDTO {
        private String name;
        private String contactNumber;
        private String relationship;
    }


    @Data
    public static class EmploymentInformationDTO {
        private String position;
        private String employeeIdNumber;
        private LocalDate dateHired;
        private LocalDate regularDateStart;
        private String companyAddress;
        private EmploymentStatus employmentStatus;
        private EmploymentType employmentType;
        private StaffType employeeType;
        private String branch;
        private String immediateSupervisor;
    }


    @Data
    public static class BenefitsDTO {

        private String sssNumber;
        private String pagibigNumber;
        private String philhealthNumber;
        private String tinNumber;

        private BigDecimal sickLeave;
        private BigDecimal vacationLeave;
        private BigDecimal paternityLeave;
        private BigDecimal maternityLeave;

        private String hmoProvider;
        private String hmoNumber;

        private BigDecimal allowance;
        private BigDecimal riceAllowance;
        private BigDecimal transportationAllowance;
        private BigDecimal communicationAllowance;
    }


    @Data
    public static class PayrollInformationDTO {

        private BigDecimal basicSalary;
        private BigDecimal dailyRate;
        private BigDecimal hourlyRate;

        private SalaryType salaryType;
        private PayrollCycle payrollCycle;
        private LocalDate effectiveDate;

        private String bankName;
        private String bankAccountNumber;
        private String bankBranch;
    }


    @Data
    public static class AccountAccessDTO {

        private String username;
        private String password;
        private String email;
        private String status;
    }
}
