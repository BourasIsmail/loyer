package ma.entraide.impot.Repository;


import ma.entraide.impot.Entity.Proprietaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProprieteRepo extends JpaRepository <Proprietaire , Long> {

}
