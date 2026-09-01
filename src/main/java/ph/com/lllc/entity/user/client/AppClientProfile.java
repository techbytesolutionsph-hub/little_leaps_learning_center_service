package ph.com.lllc.entity.user.client;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ph.com.lllc.entity.user.client.assessment.ClientInitialAssessmentSchedule;
import ph.com.lllc.entity.user.client.assignment.AppClientAssignment;
import ph.com.lllc.entity.user.client.assignment.AssignmentHistory;
import ph.com.lllc.entity.user.client.neurodev.NeurodevelopmentalAssessmentSchedule;
import ph.com.lllc.entity.user.client.pricing.AppClientServicePricing;
import ph.com.lllc.entity.user.client.schedule.ClientTherapySchedule;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.enums.DiagnosisConcern;
import ph.com.lllc.enums.EnrollmentStatus;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.util.LocalDateUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_client_profile")
@NoArgsConstructor
@AllArgsConstructor
public class AppClientProfile implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "app_client_id")
    private Long id;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "client_student_id", unique = true)
    private String clientStudentId;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "age")
    private Integer age;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "date_enrolled")
    private LocalDate dateEnrolled;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "diagnosis_concerns", columnDefinition = "jsonb")
    private Set<DiagnosisConcern> diagnosisConcerns;

    @Column(name = "program_type")
    private String programType;

    @Column(name = "branch")
    private String branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus enrollmentStatus;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_account_registered", nullable = false)
    private boolean accountRegistered = false;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Column(name = "creation_date")
    private LocalDate creationDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "last_modification_date")
    private LocalDate lastModificationDate;

    @Column(name = "last_modified_by")
    private String lastModifiedBy;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppClientAssignment> assignments;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AssignmentHistory> assignmentHistories;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppParentGuardian> appParentGuardian;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientTherapySchedule> sessionSchedules;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NeurodevelopmentalAssessmentSchedule> assessments;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ClientInitialAssessmentSchedule> assessmentSchedules;

    @JsonManagedReference
    @OneToMany(mappedBy = "appClientProfile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AppClientServicePricing> servicePricings;

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", unique = true, nullable = true)
    private AppUser appUser;

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
