package ph.com.lllc.entity.user.client.neurodev;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.enums.AssessmentStatus;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.enums.PaymentStatus;
import ph.com.lllc.util.LocalDateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Builder
@Table(name = "lllc_app_neurodevelopmental_assessment_schedule")
@NoArgsConstructor
@AllArgsConstructor
public class NeurodevelopmentalAssessmentSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "neurodev_assessment_id")
    private Long id;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "age")
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "parent_guardian")
    private String parentGuardian;

    @Column(name = "contact_number")
    private String contactNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssessmentStatus status;

    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    @Column(name = "neurodev_fee", precision = 15, scale = 2)
    private BigDecimal neurodevFee;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modification_date")
    private LocalDate lastModificationDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_client_id")
    private AppClientProfile appClientProfile;

    @PrePersist
    private void prePersist() {
        creationDate = LocalDateUtils.getLocalDate();
        createdBy = "SYSTEM";
    }

    @PreUpdate
    private void preUpdate() {
        lastModificationDate = LocalDateUtils.getLocalDate();
        lastModifiedBy = "SYSTEM";
    }
}
