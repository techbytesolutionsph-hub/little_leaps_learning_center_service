package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.assessment.NeurodevelopmentalAssessment;

@Repository
public interface NeurodevelopmentalAssessmentRepository extends JpaRepository<NeurodevelopmentalAssessment, Long> {
}
