package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.ComptePayement;
import ma.entraide.impot.Repository.ComptePayementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ComptePayementService {
    @Autowired
    private ComptePayementRepo comptePayementRepo;

    public List<ComptePayement> findAll() {
        return comptePayementRepo.findAll();
    }

    public ComptePayement findById(Long id) {
        Optional<ComptePayement> optional = comptePayementRepo.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        else throw new ResourceNotFoundException("ComptePayement not found");
    }
}
