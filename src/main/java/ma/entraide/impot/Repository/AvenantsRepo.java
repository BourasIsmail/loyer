package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.Avenant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AvenantsRepo extends JpaRepository<Avenant, Long> {
}
