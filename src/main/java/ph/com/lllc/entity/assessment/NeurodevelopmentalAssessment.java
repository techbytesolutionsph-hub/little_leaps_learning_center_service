package ph.com.lllc.entity.assessment;

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
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Builder
@Table(name = "lllc_app_neurodevelopmental_assessment")
@NoArgsConstructor
@AllArgsConstructor
public class NeurodevelopmentalAssessment {

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

    @Column(name = "referral_concern")
    private String referralConcern;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssessmentStatus status;

    @Column(name = "branch")
    private String branch;

    @Column(name = "assessment_date")
    private LocalDate assessmentDate;

    @Column(name = "assessment_time")
    private LocalTime assessmentTime;

    @Column(name = "neurodev_fee", precision = 15, scale = 2)
    private BigDecimal neurodevFee;

    @Column(name = "therapy_center_commission", precision = 15, scale = 2)
    private BigDecimal therapyCenterCommission;

    @Column(name = "assessor_name")
    private String assessorName;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "address")
    private String address;

    @Column(name = "emergency_contact")
    private String emergencyContact;

    @Column(name = "relation_to_client")
    private String relationshipToClient;

    @Column(name = "referral_source")
    private String referralSource;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "follow_up_recommendation", columnDefinition = "TEXT")
    private String followUpRecommendation;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modification_date")
    private LocalDate lastModificationDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @JsonBackReference("client-assessment")
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
