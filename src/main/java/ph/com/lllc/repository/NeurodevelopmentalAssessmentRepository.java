package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.neurodev.NeurodevelopmentalAssessmentSchedule;
import ph.com.lllc.enums.AssessmentStatus;

import java.util.List;

@Repository
public interface NeurodevelopmentalAssessmentRepository extends JpaRepository<NeurodevelopmentalAssessmentSchedule, Long> {

    List<NeurodevelopmentalAssessmentSchedule> findByStatus(AssessmentStatus assessmentStatus);
}
