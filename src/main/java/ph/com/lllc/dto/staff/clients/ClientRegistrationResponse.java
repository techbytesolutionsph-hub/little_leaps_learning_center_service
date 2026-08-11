package ph.com.lllc.dto.staff.clients;

import lombok.*;
import ph.com.lllc.enums.AssignmentStatus;
import ph.com.lllc.enums.ClientServiceType;
import ph.com.lllc.enums.EnrollmentStatus;
import ph.com.lllc.enums.Gender;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationResponse {

    Long id;
    private String uuid;

    private String clientId;

    /* Client School ID */
    private String clientStudentId;

    /* Client Information */
    private String firstName;
    private String middleName;
    private String lastName;
    private Integer age;
    private LocalDate birthDate;
    private Gender gender;

    /* Client Enrollment Details */
    private LocalDate dateEnrolled;
    private String diagnosisConcern;
    private String programType;
    private AssignmentStatus assignmentStatus;
    private String branch;
    private EnrollmentStatus enrollmentStatus;

    /* Client Account Details */
    private AccountAccessDTO accountAccess;

    /* Client Image URL */
    private String profileImageUrl;

    /* Parent / Guardian Information */
    private List<ParentGuardian> parents;


    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ParentGuardian {

        private String firstName;
        private String middleName;
        private String lastName;
        private String contactNumber;
        private String email;
        private String relationshipToClient;
        private Gender gender;
        private String address;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountAccessDTO {

        private String username;
        private String password;
        private String email;
        private String status;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ClientServicePricingDTO {

        private ClientServiceType serviceType;
        private BigDecimal price;
        private LocalDate effectiveDate;
        private String notes;
    }
}