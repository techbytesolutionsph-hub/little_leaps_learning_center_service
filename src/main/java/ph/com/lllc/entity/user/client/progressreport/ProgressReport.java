package ph.com.lllc.entity.user.client.progressreport;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ph.com.lllc.util.LocalDateUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "lllc_app_client_progress_report")
@NoArgsConstructor
@AllArgsConstructor
public class ProgressReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "progress_report_id")
    private String progressReportId;

    @Column(name = "reporting_period_from")
    private LocalDate reportingPeriodFrom;

    @Column(name = "reporting_period_to")
    private LocalDate reportingPeriodTo;

    @Column(name = "notes", length = 1000)
    private String notes;

    /* Cloudinary uploaded file details */
    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "file_size")
    private Long fileSize;

    /* Case manager/Therapist ID details */
    @Column(name = "assignee_id")
    private String assigneeId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "creation_date")
    private LocalDateTime creationDate;

    @Column(name = "created_by")
    private String createdBy;


    @PrePersist
    private void prePersist() {
        creationDate = LocalDateUtils.getLocalDateTime();
        createdBy = this.assigneeId;
    }
}
