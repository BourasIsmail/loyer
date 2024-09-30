package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Proprietaire;
import ma.entraide.impot.Entity.Province;
import ma.entraide.impot.Repository.LocalRepo;
import ma.entraide.impot.Repository.ProprieteRepo;
import ma.entraide.impot.Repository.ProvinceRepo;
import ma.entraide.impot.Repository.TscRepo;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service

public class LocalService {
    @Autowired
    private LocalRepo localRepo;
    @Autowired
    private ProprieteService proprieteService;
    @Autowired
    private ProvinceService provinceService;


    public List<Local> getAll() {
        return localRepo.findAll();
    }

    public Local getById(Long id) {
        Optional<Local> local = localRepo.findById(id);
        if (local.isPresent()) {
            return local.get();
        }
        else {
            throw new ResourceNotFoundException();
        }
    }

    public Local addLocal(Local local) {
        if(local.getRib().length()<24){
            throw new IllegalArgumentException();
        }
        Province province = provinceService.getProvinceById(local.getProvince().getId());
        local.setProvince(province);

        List<Proprietaire> proprietaires = local.getProprietaires();
        List<Proprietaire> proprietairesNew = new ArrayList<>();
        for (Proprietaire proprietaire : proprietaires) {
            proprietairesNew.add(proprieteService.getById(proprietaire.getId()));
        }
        local.setProprietaires(proprietairesNew);

        return localRepo.save(local);
    }

    public Local updateLocal(Long id ,Local local) {
        Local newLocal = getById(id);
        Province province = provinceService.getProvinceById(local.getProvince().getId());
        newLocal.setProvince(province);

        List<Proprietaire> proprietaires = local.getProprietaires();
        List<Proprietaire> proprietairesNew = new ArrayList<>();
        for (Proprietaire proprietaire : proprietaires) {
            proprietairesNew.add(proprieteService.getById(proprietaire.getId()));
        }
        newLocal.setProprietaires(proprietairesNew);
        newLocal.setAdresse(local.getAdresse());
        newLocal.setRib(local.getRib());
        newLocal.setEtat(local.getEtat());
        newLocal.setBrutMensuel(local.getBrutMensuel());
        return localRepo.save(newLocal);
    }

    public String deleteLocal(Long id) {
        localRepo.deleteById(id);
        return "deleted";
    }

    public List<Local> getLocalByDelegation(Long id) {
        return localRepo.findByDelegationId(id);
    }

    public List<Local> getLocalByProprietaires(Long id) {
        return localRepo.findByProprietaireId(id);
    }

    public List<Local> getLocalByRegion(Long id) {
        return localRepo.findByRegionId(id);
    }


}
