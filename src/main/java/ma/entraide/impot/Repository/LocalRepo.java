package ma.entraide.impot.Repository;


import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LocalRepo extends JpaRepository<Local, Long> {
    @Query("SELECT d FROM Local d WHERE d.province.region.id = :id")
    public List<Local> findByRegionId(@Param("id") Long id);

    @Query("SELECT d FROM Local d WHERE d.province.id = :id")
    public List<Local> findByDelegationId(@Param("id") Long id);

    @Query("SELECT l FROM Local l JOIN l.proprietaires p WHERE p.id = :id")
    List<Local> findByProprietaireId(@Param("id") Long id);
}
