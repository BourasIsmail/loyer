package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository

public interface RegionRepo extends JpaRepository<Region, Long> {
}
