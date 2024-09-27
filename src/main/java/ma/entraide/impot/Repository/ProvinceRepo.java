package ma.entraide.impot.Repository;

import ma.entraide.impot.Entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProvinceRepo  extends JpaRepository<Province, Long> {
    @Query("SELECT d FROM Province d WHERE d.region.id = :id")
    public List<Province> findByRegionId(@Param("id") Long id);
}
