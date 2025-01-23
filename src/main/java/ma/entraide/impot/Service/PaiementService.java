package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.*;
import ma.entraide.impot.Repository.ConfirmedPaymentRepo;
import ma.entraide.impot.Repository.PaiementRepo;
import ma.entraide.impot.Repository.RASConfigRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
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

    @Autowired
    private ConfirmedPaymentRepo confirmedPaymentRepo;

    @Autowired
    private RASConfigRepo rasConfigRepo;

    public Paiement getById(Long id) {
        Optional<Paiement> paiementOptional = paiementRepo.findById(id);
        if (paiementOptional.isPresent()) {
            return paiementOptional.get();
        } else {
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
        int rasP = calcPourcentageRAS(brutAnnuel, isPersonnemoral(local.getProprietaires()), local.getId());

        //montant de ras
        double ras = Math.ceil(calcRAS(mensuelBrute, rasP));

        //montant net
        double mtNet = mensuelBrute - ras;

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

    public Paiement payerLocal(Local localPaye, Date date) {
        Local local = localService.getById(localPaye.getId());
        double bruteMensuel = local.getBrutMensuel();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        String periode = month + "/" + year;

        int rasP = 0;
        double ras = 0;
        double mtNet = 0;
        String rib = "";

        // Check if a ConfirmedPayment exists for this local and date
        ConfirmedPayment confirmedPayment = confirmedPaymentRepo.findConfirmedPaymentByDateAndLocal(local.getId(), periode);

        if (confirmedPayment != null) {
            // Use values from ConfirmedPayment
            bruteMensuel = confirmedPayment.getMontantBrute();
            ras = confirmedPayment.getRas();
            rasP = (int) confirmedPayment.getTaux();
            mtNet = confirmedPayment.getMontantNetPaye();
            rib = confirmedPayment.getRib();
        } else if (local.getEtat().equals("actif") || (local.getEtat().equals("résilié") && local.getModeDePaiement().equals("virement"))) {
            // Calculate values using RASConfig
            double brutAnnuel = bruteMensuel * 12;
            rasP = calcPourcentageRAS(brutAnnuel, isPersonnemoral(local.getProprietaires()), local.getId());
            ras = Math.ceil(calcRAS(bruteMensuel, rasP));
            mtNet = bruteMensuel - ras;
            rib = local.getRib();
        }

        return new Paiement(date, month, year, periode, rib, bruteMensuel, rasP, ras, mtNet, local);
    }

    public byte[] payerEnMasse(List<Local> local, Date date) throws IOException {
        List<Paiement> paiements = new ArrayList<>();
        for (Local l : local) {
            paiements.add(payerLocal(l, date));
        }
        return generatePdfEtat(paiements);
    }

    public byte[] genOv(List<Local> local, Date date, String nOrdre, String nOP, String dateCreation,
                        ComptePayement comptePayement, String mode) throws IOException {
        List<Paiement> paiements = new ArrayList<>();
        for (Local l : local) {
            paiements.add(payerLocal(l, date));
        }
        return generateOV(paiements, nOrdre, nOP, dateCreation, comptePayement, mode);
    }

    public int calcPourcentageRAS(double brutAnnuel, boolean isMoral, Long localId) {
        if (isMoral) {
            return 0;
        }

        RASConfig config = rasConfigRepo.findFirstByOrderByIdDesc();
        if (config == null) {
            throw new ResourceNotFoundException("RAS configuration not found");
        }

        // Check for special case
        Integer specialCasePercentage = config.getSpecialCasePercentages().get(localId);
        if (specialCasePercentage != null) {
            return specialCasePercentage;
        }

        if (brutAnnuel < config.getLowerThreshold1()) {
            return 0;
        } else if (brutAnnuel >= config.getLowerThreshold1() && brutAnnuel < config.getUpperThreshold1()) {
            return config.getPercentage1();
        } else if (brutAnnuel >= config.getLowerThreshold2() && brutAnnuel < config.getUpperThreshold2()) {
            return config.getPercentage2();
        } else {
            return config.getPercentage3();
        }
    }

    public double calcRAS(double mt, int ras) {
        return (mt * ras) / 100;
    }

    public boolean isPersonnemoral(List<Proprietaire> proprietaireList) {
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

    public double payer(double montant) {
        RASConfig config = rasConfigRepo.findFirstByOrderByIdDesc();
        if (config == null) {
            throw new ResourceNotFoundException("RAS configuration not found");
        }

        double brutAnnuel = montant * 12;
        int rasP;

        if (brutAnnuel < config.getLowerThreshold1()) {
            rasP = 0;
        } else if (brutAnnuel >= config.getLowerThreshold1() && brutAnnuel < config.getUpperThreshold1()) {
            rasP = config.getPercentage1();
        } else if (brutAnnuel >= config.getLowerThreshold2() && brutAnnuel < config.getUpperThreshold2()) {
            rasP = config.getPercentage2();
        } else {
            rasP = config.getPercentage3();
        }

        double ras = Math.ceil(calcRAS(montant, rasP));
        double mtNet = montant - ras;

        return mtNet;
    }
}

