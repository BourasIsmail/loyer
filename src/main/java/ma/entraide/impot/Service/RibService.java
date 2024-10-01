package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.Rib;
import ma.entraide.impot.Repository.RibRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RibService {
    @Autowired
    private RibRepo repo;

    public Rib getRib(Long id) {
        Optional<Rib> rib = repo.findById(id);
        if (rib.isPresent()) {
            return rib.get();
        }
        else {
            throw new ResourceNotFoundException("Rib with id " + id + " not found");
        }
    }

    public Rib addRib(Rib rib) {
        return repo.save(rib);
    }

    public void deleteRib(Rib rib) {
        Rib rib1 = getRib(rib.getId());
        repo.delete(rib1);
    }

    public Rib updateRib(Rib rib) {
        Rib rib1 = getRib(rib.getId());
        rib1.setRib(rib.getRib());
        return repo.save(rib1);
    }

    public List<Rib> addRibs(List<Rib> ribs) {
        return repo.saveAll(ribs);
    }

    public String deleteRibs(List<Rib> ribs) {
        repo.deleteAll(ribs);
        return "Deleted " + ribs.size() + " Ribs";
    }
}
