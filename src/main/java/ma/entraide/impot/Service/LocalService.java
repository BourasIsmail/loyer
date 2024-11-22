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
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Date;
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
        newLocal.setDateResiliation(local.getDateResiliation());
        newLocal.setDateEffetContrat(local.getDateEffetContrat());
        newLocal.setLatitude(local.getLatitude());
        newLocal.setLongitude(local.getLongitude());
        newLocal.setBrutMensuel(local.getBrutMensuel());
        newLocal.setIdContrat(local.getIdContrat());
        newLocal.setModeDePaiement(local.getModeDePaiement());
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

    public Local uploadContact(Long id , MultipartFile file){
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        try{
            if(fileName.contains("..")){
                throw new Exception("Filemane contains invalid path sequence"
                        + fileName);
            }
            Local local = getById(id);
            local.setFileName(fileName);
            local.setFileType(file.getContentType());
            local.setContrat(file.getBytes());
            return localRepo.save(local);
        }catch (Exception e){
            throw new ResourceNotFoundException("File not uploaded"+fileName);
        }
    }

    public Object dashboard(){
        Object data = new Object(){
            public long totalLocaux = localRepo.count();
            public long locauxIncomplet = localRepo.countIncompleteData();
            public long localComplet = totalLocaux - locauxIncomplet;

            public long localActif = localRepo.countEtatActif();
            public long  localResilie = localRepo.countEtatResilie();
            public long localSuspendue = localRepo.countEtatSuspendue();

            public long totalProprietaire = localRepo.countProprietaires();
            public long proprietaireIncomplet = localRepo.countProprietairesIncomplete();
            public long proprietaireComplet = totalProprietaire - proprietaireIncomplet;
            public long personnesMorale = localRepo.countPersonnesMorale();
            public long personnesPhysique = localRepo.countPersonnesPhysique();
        };
        return data;
    }

}
