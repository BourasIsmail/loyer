package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.Paiement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaiementRepo extends JpaRepository<Paiement, Long> {
    @Query("SELECT d FROM Paiement d WHERE d.local.id = :id")
    public List<Paiement> findByLocal(@Param("id") Long id);
}
