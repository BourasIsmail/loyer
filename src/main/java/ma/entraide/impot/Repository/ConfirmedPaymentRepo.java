package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.ConfirmedPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ConfirmedPaymentRepo extends JpaRepository<ConfirmedPayment, Long> {
    @Query("select c from ConfirmedPayment c where c.local.id = :id and c.moisAnnee = :moidAnnee")
    public ConfirmedPayment findConfirmedPaymentByDateAndLocal(@Param("id") Long id, @Param("moidAnnee") String moidAnnee);

    @Query("select c from ConfirmedPayment c where c.local.id = :id")
    public List<ConfirmedPayment> findConfirmedPaymentByLocalId(@Param("id") Long id);
}
