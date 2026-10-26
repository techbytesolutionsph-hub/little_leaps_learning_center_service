package ph.com.lllc.repository.hris;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ph.com.lllc.entity.user.hris.AppApplicantOffer;

@Repository
public interface AppApplicantOfferRepository extends JpaRepository<AppApplicantOffer, Long> {
}
