package ph.com.lllc.entity.user.hris;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.enums.EmploymentType;
import ph.com.lllc.enums.OfferCallMethod;
import ph.com.lllc.enums.OfferCallStatus;
import ph.com.lllc.enums.OfferStatus;
import ph.com.lllc.util.LocalDateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(
        name = "lllc_app_applicant_offer",
        indexes = {
                @Index(name = "idx_offer_candidate", columnList = "applicant_id"),
                @Index(name = "idx_offer_status", columnList = "offer_status"),
                @Index(name = "idx_offer_call_date", columnList = "call_date")
        }
)
@NoArgsConstructor
public class AppApplicantOffer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "offer_number", nullable = false, unique = true, length = 30)
    private String offerNumber;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "applicant_id", nullable = false)
    private AppApplicant applicant;

    @Column(name = "position_title", nullable = false, length = 150)
    private String positionTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "employment_type", nullable = false, length = 30)
    private EmploymentType employmentType;

    @Column(name = "proposed_salary", nullable = false, precision = 12, scale = 2)
    private BigDecimal proposedSalary;

    @Column(name = "salary_period", nullable = false, length = 20)
    private String salaryPeriod = "MONTHLY";

    @Column(name = "work_location", nullable = false, length = 255)
    private String workLocation;

    @Column(name = "proposed_start_date", nullable = false)
    private LocalDate proposedStartDate;

    @Column(name = "offer_date", nullable = false)
    private LocalDate offerDate;

    @Column(name = "expiration_date")
    private LocalDate expirationDate;

    @Column(name = "offer_letter_url", length = 1000)
    private String offerLetterUrl;

    @Column(name = "offer_letter_public_id", length = 255)
    private String offerLetterPublicId;

    @Enumerated(EnumType.STRING)
    @Column(name = "offer_status", nullable = false, length = 30)
    private OfferStatus offerStatus = OfferStatus.DRAFT;

    @Column(name = "call_date")
    private LocalDate callDate;

    @Column(name = "call_time")
    private LocalTime callTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_method", length = 30)
    private OfferCallMethod callMethod;

    @Column(name = "meeting_link", length = 1000)
    private String meetingLink;

    @Column(name = "call_notes", columnDefinition = "TEXT")
    private String callNotes;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_status", length = 30)
    private OfferCallStatus callStatus = OfferCallStatus.NOT_SCHEDULED;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt;

    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;

    @Column(name = "decline_reason", columnDefinition = "TEXT")
    private String declineReason;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modification_date")
    private LocalDateTime lastModificationDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @PrePersist
    private void prePersist() {
        creationDate = LocalDateUtils.getLocalDateTime();
        createdBy = "SYSTEM";
    }

    @PreUpdate
    private void preUpdate() {
        lastModificationDate = LocalDateUtils.getLocalDateTime();
        lastModifiedBy = "SYSTEM";
    }
}
