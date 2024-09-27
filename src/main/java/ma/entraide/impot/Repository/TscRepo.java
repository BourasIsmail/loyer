package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.Tsc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TscRepo  extends JpaRepository<Tsc, Long> {
}
