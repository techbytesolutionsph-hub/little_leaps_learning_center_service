package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.assignment.AppClientAssignment;

@Repository
public interface AppClientAssignmentRepository extends JpaRepository<AppClientAssignment, Long> {
}
