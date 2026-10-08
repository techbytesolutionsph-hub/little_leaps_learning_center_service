package ph.com.lllc.entity.user.hris;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import ph.com.lllc.enums.hris.ApplicantStatus;

import java.io.Serializable;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_applicant")
@NoArgsConstructor
@AllArgsConstructor
public class AppApplicant implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "applicant_id")
    private Long id;

    @Column(name = "first_name", nullable = false)
    private String firstName;

    @Column(name = "middle_name")
    private String middleName;

    @Column(name = "last_name", nullable = false)
    private String lastName;

    @Column(name = "suffix")
    private String suffix;

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "date_of_application")
    private LocalDate dateOfApplication;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private ApplicantStatus status;

    @JsonManagedReference
    @OneToOne(mappedBy = "applicant", cascade = CascadeType.ALL, orphanRemoval = true)
    private AppApplicantOnboarding onboarding;

    public void setOnboarding(AppApplicantOnboarding onboarding) {
        this.onboarding = onboarding;

        if (onboarding != null && onboarding.getApplicant() != this) {
            onboarding.setApplicant(this);
        }
    }
}
