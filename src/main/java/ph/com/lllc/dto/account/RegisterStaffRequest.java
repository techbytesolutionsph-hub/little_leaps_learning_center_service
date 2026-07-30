package ph.com.lllc.dto.account;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.enums.StaffType;
import ph.com.lllc.enums.UserRole;

import java.time.LocalDate;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request payload for registering a staff account")
public class RegisterStaffRequest {

    @Schema(
            description = "Unique username for login",
            example = "superadmin",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String username;


    @Schema(
            description = "Initial password for the staff account",
            example = "Password@1234",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String password;


    @Schema(
            description = "Staff email address",
            example = "superadmin@littleleapslearningcenter.com",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String email;


    @Schema(
            description = "Staff first name",
            example = "Glenn Mark"
    )
    private String firstName;


    @Schema(
            description = "Staff middle name",
            example = "Trampe"
    )
    private String middleName;


    @Schema(
            description = "Staff last name",
            example = "Anduiza"
    )
    private String lastName;


    @Schema(
            description = "Staff contact number",
            example = "09106121529"
    )
    private String phoneNumber;


    @Schema(
            description = "Staff date of birth",
            example = "1988-10-18"
    )
    private LocalDate dateOfBirth;

    @Schema(
            description = "Staff gender",
            example = "MALE",
            allowableValues = {
                    "MALE",
                    "FEMALE"
            }
    )
    private Gender gender;

    @Schema(
            description = "Complete residential address",
            example = "Blk 2 Lot 12 UC2 Brgy Citrus, City of San Jose Del Monte Bulacan"
    )
    private String completeAddress;


    @Schema(
            description = "Profile image URL",
            example = "https://res.cloudinary.com/dx8es7rbm/image/upload/v1777795180/cuichsukdl1fcqwlj39b.jpg"
    )
    private String profileImageUrl;


    @Schema(
            description = "Staff classification",
            example = "ADMINISTRATOR",
            allowableValues = {
                    "ADMINISTRATOR",
                    "BUSINESS_OWNER",
                    "CASE_MANAGER",
                    "SECRETARY",
                    "BEHAVIORAL_THERAPIST"
            }
    )
    private StaffType staffType;


    @Schema(
            description = "Date when staff was hired",
            example = "2026-07-30"
    )
    private LocalDate dateHired;


    @Schema(
            description = "Professional title of staff",
            example = "Application Developer - Cloud Fullstack"
    )
    private String professionalTitle;


    @Schema(
            description = "Assigned branch location",
            example = "San Juan Batangas"
    )
    private String branchAssign;


    @Schema(
            description = "Professional license number",
            example = "IBM-123456"
    )
    private String licenseNumber;


    @Schema(
            description = "Area of specialization",
            example = "Web Development"
    )
    private String specialization;


    @Schema(
            description = "User role assigned after registration",
            example = "SUPER_ADMIN",
            allowableValues = {
                    "SUPER_ADMIN",
                    "CASE_MANAGER",
                    "ACCOUNTING",
                    "SECRETARY",
                    "THERAPIST",
                    "EMPLOYEE",
                    "HR"
            }
    )
    private UserRole role;
}