package ma.entraide.impot.Repository;


import ma.entraide.impot.Entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProprieteRepo extends JpaRepository <Proprietaire , Long> {
    @Query("select p from Proprietaire p where p.province.region.id = :id")
    List<Proprietaire> findByProvinceRegion(@Param("id") Long id);
}
