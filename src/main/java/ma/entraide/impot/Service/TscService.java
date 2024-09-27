package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Tsc;
import ma.entraide.impot.Repository.TscRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class TscService {
    @Autowired
    private TscRepo tscRepository;

    @Autowired
    private LocalService localService;

    public List<Tsc> getAll() {
        return tscRepository.findAll();
    }

    public Tsc findById(Long id) {
        Optional<Tsc> tscOptional = tscRepository.findById(id);
        if (tscOptional.isPresent()) {
            return tscOptional.get();
        }
        else{
            throw new ResourceNotFoundException("tsc not found");
        }
    }

    public Tsc createTsc(Tsc tsc) {
        Local local = localService.getById(tsc.getLocal().getId());
        return tscRepository.save(tsc);
    }

    public Tsc updateTsc(Long id,Tsc tsc) {
        Tsc newTsc = findById(id);
        newTsc.setId(tsc.getId());
        newTsc.setLocal(localService.getById(tsc.getLocal().getId()));
        newTsc.setType(tsc.getType());
        return tscRepository.save(newTsc);
    }

    public String deleteTsc(Long id) {
        tscRepository.deleteById(id);
        return "Deleted tsc";
    }


}
