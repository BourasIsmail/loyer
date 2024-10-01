package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.Rib;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RibRepo extends JpaRepository<Rib, Long> {
}
