package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.Proprietaire;
import ma.entraide.impot.Entity.Province;
import ma.entraide.impot.Entity.Rib;
import ma.entraide.impot.Repository.ProprieteRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class ProprieteService {
    @Autowired
    private ProprieteRepo proprieteRepo;

    @Autowired
    private RibService ribService;

    @Autowired
    private ProvinceService provinceService;

    public List<Proprietaire> getAll() {
        return proprieteRepo.findAll();
    }

    public Proprietaire getById(Long id) {
        Optional<Proprietaire> proprietaire = proprieteRepo.findById(id);
        if (proprietaire.isPresent()) {
            return proprietaire.get();
        }
        else{
            throw new ResourceNotFoundException("propriete n'existe pas");
        }
    }

    public Proprietaire add(Proprietaire proprietaire) {
        Province province = provinceService.getProvinceById(proprietaire.getProvince().getId());
        proprietaire.setProvince(province);
        List<Rib> ribs = ribService.addRibs(proprietaire.getRib());



        return proprieteRepo.save(proprietaire);
    }

    public Proprietaire update(Long id, Proprietaire proprietaire) {
        Proprietaire p = getById(id);
        Province province = provinceService.getProvinceById(proprietaire.getProvince().getId());
        p.setProvince(province);
        p.setNomComplet(proprietaire.getNomComplet());
        p.setCin(proprietaire.getCin());
        p.setTelephone(proprietaire.getTelephone());
        p.setType(proprietaire.getType());
        p.setAdresse(proprietaire.getAdresse());
        return proprieteRepo.save(p);
    }

    public String delete(Long id) {
        proprieteRepo.deleteById(id);
        return "deleted";
    }



}
