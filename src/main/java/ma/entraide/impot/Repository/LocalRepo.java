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

    @Query("select count (l) from Local l")
    public long count();

    @Query("SELECT count(l) FROM Local l WHERE l.brutMensuel = 0 OR l.rib = '0' OR l.adresse = 'pas d''adresse' ")
    public long countIncompleteData();


    @Query("SELECT count(p) FROM Proprietaire p")
    public long countProprietaires();

    @Query("select count(p) from Proprietaire p where p.adresse IS NULL or p.cin IS NULL or p.telephone IS NULL")
    public long countProprietairesIncomplete();

    @Query("select count(p) from Proprietaire p where p.type = 'personne morale'")
    public long countPersonnesMorale();

    @Query("select count(p) from Proprietaire p where p.type = 'personne physique'")
    public long countPersonnesPhysique();

    @Query("select count(l) from Local l where l.etat = 'actif'")
    public long countEtatActif();

    @Query("select count(l) from Local l where l.etat = 'résilié'")
    public long countEtatResilie();

    @Query("select count(l) from Local l where l.etat = 'suspendue'")
    public long countEtatSuspendue();
}
