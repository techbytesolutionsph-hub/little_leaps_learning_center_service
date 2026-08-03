package ph.com.lllc.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.transaction.annotation.Transactional;
import ph.com.lllc.entity.user.common.AppRolePermission;
import ph.com.lllc.entity.user.common.AppUserRole;

@Repository
public interface AppRolePermissionRepository extends JpaRepository<AppRolePermission, Long> {

    @Modifying
    @Transactional
    @Query("DELETE FROM AppRolePermission rp WHERE rp.userRole = :userRole")
    void deleteByUserRole(@Param("userRole") AppUserRole userRole);
}
