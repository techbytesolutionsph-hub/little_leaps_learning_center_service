package ph.com.lllc.repository.holiday;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.holiday.PhilippineHoliday;

@Repository
public interface PhilippineHolidayRepository extends JpaRepository<PhilippineHoliday, Long> {

    @Modifying
    @Transactional
    @Query(value = "TRUNCATE TABLE lllc_app_philippine_holiday RESTART IDENTITY", nativeQuery = true)
    void truncateTable();

    boolean existsByDate(String date);

    boolean existsByDateStartingWith(String year);
}
