package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.RASConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RASConfigRepo extends JpaRepository<RASConfig, Long> {
    RASConfig findFirstByOrderByIdDesc();
}

