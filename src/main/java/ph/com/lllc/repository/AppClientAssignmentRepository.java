package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.client.AppClientProfile;
import ph.com.lllc.entity.user.client.assignment.AppClientAssignment;
import ph.com.lllc.enums.AssignmentStatus;

import java.util.Optional;

@Repository
public interface AppClientAssignmentRepository extends JpaRepository<AppClientAssignment, Long> {

    Optional<AppClientAssignment> findFirstByAppClientProfileAndStatusOrderByAssignedAtDesc(AppClientProfile appClientProfile, AssignmentStatus status);
}
