package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.common.AppRoleMapping;

import java.util.List;

@Repository
public interface AppRoleMappingRepository extends JpaRepository<AppRoleMapping, Long> {
    List<AppRoleMapping> findByRole(String role);
}
