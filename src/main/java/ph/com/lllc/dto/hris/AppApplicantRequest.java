package ph.com.lllc.dto.hris;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import ph.com.lllc.enums.hris.ApplicantStatus;

import java.time.LocalDate;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Applicant Request")
public class AppApplicantRequest {

    @Schema(
            description = "Applicant ID",
            example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private Long id;

    @Schema(
            description = "First name",
            example = "Heero",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String firstName;

    @Size(max = 100, message = "Middle name must not exceed 100 characters")
    @Schema(
            description = "Middle name",
            example = "Anduiza"
    )
    private String middleName;

    @NotBlank(message = "Last name is required")
    @Size(max = 100, message = "Last name must not exceed 100 characters")
    @Schema(
            description = "Last name",
            example = "Yuy",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String lastName;

    @Size(max = 20, message = "Suffix must not exceed 20 characters")
    @Schema(
            description = "Name suffix",
            example = "Jr."
    )
    private String suffix;

    @Email(message = "Invalid email address")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    @Schema(
            description = "Applicant email address",
            example = "heero.yuy@gmail.com"
    )
    private String email;

    @Pattern(
            regexp = "^(09|\\+639)\\d{9}$",
            message = "Invalid Philippine phone number"
    )
    @Schema(
            description = "Applicant phone number",
            example = "09171234567"
    )
    private String phoneNumber;

    @Schema(
            description = "Date when the applicant applied. If not provided, the current date will be used.",
            example = "2026-10-05"
    )
    private LocalDate dateOfApplication;

    @Schema(
            description = "Current applicant status",
            example = "APPLIED",
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private ApplicantStatus status;
}
