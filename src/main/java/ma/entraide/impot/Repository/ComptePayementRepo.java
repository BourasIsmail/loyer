package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.ComptePayement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ComptePayementRepo extends JpaRepository<ComptePayement, Long> {
}
