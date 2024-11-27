package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.*;
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

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service

public class LocalService {
    @Autowired
    private LocalRepo localRepo;
    @Autowired
    private ProprieteService proprieteService;
    @Autowired
    private ProvinceService provinceService;
    @Autowired
    private PaiementService paiementService;
    @Autowired
    private ConfirmedPaymentService confirmedPaymentService;
    @Autowired
    private AvenantService avenantService;



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

    public Local updateLocal(Long id, Local local) {
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
        newLocal.setIdContrat(local.getIdContrat());
        newLocal.setModeDePaiement(local.getModeDePaiement());

        double aBrute = newLocal.getBrutMensuel();

        newLocal.setBrutMensuel(local.getBrutMensuel());

        if (aBrute != newLocal.getBrutMensuel()) {
            newLocal.setAncientBrute(aBrute);
            newLocal.setBrutMensuel(local.getBrutMensuel());
            newLocal.setDateChangementBrute(local.getDateChangementBrute());

            Date date = newLocal.getDateChangementBrute();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            // get month and year
            int month = localDate.getMonthValue();
            int year = localDate.getYear();

            List<ConfirmedPayment> cp = confirmedPaymentService.getConfirmedPaymentByLocal(newLocal.getId());
            if(!cp.isEmpty()){
                double total = 0;
                for (ConfirmedPayment cl : cp) {

                    if (cl.getYear() <year || (cl.getYear()==year && cl.getMois()<=month)) {
                        Paiement p = paiementService.payerLocal(cl.getLocal(),cl.getDate());
                        double pPre = paiementService.payer(newLocal.getAncientBrute());
                        double av = p.getNetMensuel()-pPre;
                        total +=av;
                    }
                }
                avenantService.addAvenant(new Avenant(newLocal,total));
            }
        }

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
    public List<Local> getConfirmedLocalsByDateAndRegion(Date date, String regionName) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int month = cal.get(Calendar.MONTH) + 1; // Calendar.MONTH is zero-based
        int year = cal.get(Calendar.YEAR);

        return localRepo.findConfirmedLocalsByMonthYearAndRegion(month, year, regionName);
    }
}
