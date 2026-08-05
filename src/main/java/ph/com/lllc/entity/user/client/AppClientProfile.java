package ph.com.lllc.entity.user.client;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.entity.assessment.ClientInitialAssessmentSchedule;
import ph.com.lllc.entity.neurodev.NeurodevelopmentalAssessmentSchedule;
import ph.com.lllc.entity.schedule.ClientTherapySchedule;
import ph.com.lllc.entity.user.common.AppUser;
import ph.com.lllc.enums.EnrollmentStatus;
import ph.com.lllc.enums.Gender;
import ph.com.lllc.util.LocalDateUtils;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.List;

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

    @Column(name = "diagnosis_concern")
    private String diagnosisConcern;

    @Column(name = "program_type")
    private String programType;

    @Column(name = "branch")
    private String branch;

    @Column(name = "case_manager")
    private String caseManager;

    @Column(name = "therapist")
    private String therapist;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private EnrollmentStatus enrollmentStatus;

    @Column(name = "is_active")
    private boolean isActive = true;

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

    @JsonBackReference
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "app_user_id", unique = true, nullable = false)
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
