package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.ComptePayement;
import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Paiement;
import ma.entraide.impot.Entity.Proprietaire;
import ma.entraide.impot.Repository.PaiementRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.*;

import static ma.entraide.impot.Service.PdfGenerator.generateOV;
import static ma.entraide.impot.Service.PdfGenerator.generatePdfEtat;

@Service
public class PaiementService {
    @Autowired
    private PaiementRepo paiementRepo;

    @Autowired
    private LocalService localService;

    @Autowired
    private ComptePayementService comptePayementService;

    public Paiement getById(Long id) {
        Optional<Paiement> paiementOptional = paiementRepo.findById(id);
        if (paiementOptional.isPresent()) {
            return paiementOptional.get();
        }
        else{
            throw new ResourceNotFoundException("paiement not found");
        }
    }

    public List<Paiement> getAll() {
        return paiementRepo.findAll();
    }

    public List<Paiement> getByLocal(Long id) {
        return paiementRepo.findByLocal(id);
    }

    public Paiement save(Paiement paiement) {
        Local local = localService.getById(paiement.getLocal().getId());

        double mensuelBrute = local.getBrutMensuel();

        Date date = paiement.getDateCreation();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //get month and year
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        String periode = String.format("%02d/%d", month, year);

        //pourcentage ras
        double brutAnnuel = mensuelBrute * 12;
        int rasP = calcPourcentageRAS(brutAnnuel, isPersonnemoral(local.getProprietaires()));

        //montant de ras
        double ras = Math.ceil(calcRAS(mensuelBrute,rasP));

        //montant net
        double mtNet = mensuelBrute-ras;


        paiement.setBruteMensuel(mensuelBrute);
        paiement.setRas(ras);
        paiement.setPourcentageRAS(rasP);
        paiement.setNetMensuel(mtNet);
        paiement.setMonth(month);
        paiement.setYear(year);
        paiement.setMoisAnnee(periode);
        paiement.setLocal(local);
        return paiementRepo.save(paiement);
    }

    public Paiement add(Paiement paiement) {
        return paiementRepo.save(paiement);
    }

    public Paiement payerLocal(Local localPaye, Date date){
        Local local = localService.getById(localPaye.getId());
        double bruteMensuel = local.getBrutMensuel();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //get month and year
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        String periode = month +"/"+year;

        //
        int rasP = 0;
        double ras = 0;
        double mtNet = 0;

        if(local.getEtat().equals("actif") || local.getEtat().equals("résilié") && local.getModeDePaiement().equals("virement")){
            //pourcentage ras
            double brutAnnuel = bruteMensuel * 12;
             rasP = calcPourcentageRAS(brutAnnuel, isPersonnemoral(local.getProprietaires()));
             if(local.getId() == 3809  && rasP<=10){
                 rasP = 10;
             }
            //montant de ras
             ras = Math.ceil(calcRAS(bruteMensuel,rasP));

            //montant net
             mtNet = bruteMensuel-ras;
        }


        return new Paiement(date, month, year, periode, bruteMensuel, rasP, ras, mtNet, local);
    }

    public byte[] payerEnMasse(List<Local> local, Date date) throws IOException {
        List<Paiement> paiements = new ArrayList<>();
        for (Local l : local) {
                paiements.add(payerLocal(l, date));
        }
        return generatePdfEtat(paiements);
    }

    public byte[] genOv(List<Local> local, Date date, String nOrdre, String nOP, String dateCreation
    , ComptePayement comptePayement, String mode) throws IOException {
        List<Paiement> paiements = new ArrayList<>();
        for (Local l : local) {
            paiements.add(payerLocal(l, date));
        }
        return generateOV(paiements, nOrdre, nOP, dateCreation, comptePayement, mode);
    }

    public int calcPourcentageRAS(double mt, boolean isMoral){
        if(mt<30000 || isMoral){
            return 0;
        }
        else if(mt>=30000 && mt<120000){
            return 10;
        }
        else if(mt>=120000 ){
            return 15;
        }
        return 0;
    }

    public double calcRAS(double mt , int ras){
        return (mt * ras) / 100;
    }

    public boolean isPersonnemoral(List<Proprietaire> proprietaireList){
        for (Proprietaire proprietaire : proprietaireList) {
            if (proprietaire.getType().equals("personne morale")) {
                return true;
            }
        }
        return false;
    }

    public byte[] etatPdf(List<Paiement> paiements) throws IOException {
        return generatePdfEtat(paiements);
    }

    public double payer(double montant){

        //
        int rasP = 0;
        double ras = 0;
        double mtNet = 0;

            //pourcentage ras
            double brutAnnuel = montant * 12;
        if(brutAnnuel<30000 ){
            rasP = 0;
        }
        else if(brutAnnuel>=30000 && brutAnnuel<120000){
            rasP = 10;
        }
        else if(brutAnnuel>=120000 ){
            rasP = 15;
        }

            //montant de ras
            ras = Math.ceil(calcRAS(montant,rasP));

            //montant net
            mtNet = montant-ras;

        return mtNet;
    }



}
