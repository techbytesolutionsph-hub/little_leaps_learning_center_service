package ph.com.lllc.dto.staff.clients;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ph.com.lllc.enums.AssessmentStatus;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NeurodevAssessmentResponse {

    private Long id;
    private String clientName;
    private Integer age;
    private Gender gender;
    private String parentGuardian;
    private String contactNumber;
    private AssessmentStatus status;
    private LocalDate assessmentDate;
    private BigDecimal neurodevFee;
    private PaymentStatus paymentStatus;
    private String notes;
    private LocalDate creationDate;
    private String createdBy;
    private LocalDate lastModificationDate;
    private String lastModifiedBy;
}
