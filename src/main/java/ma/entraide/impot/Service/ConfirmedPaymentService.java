package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.ConfirmedPayment;
import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Repository.ConfirmedPaymentRepo;
import ma.entraide.impot.Repository.LocalRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ConfirmedPaymentService {
    @Autowired
    private ConfirmedPaymentRepo confirmedPaymentRepo;

    @Autowired
    private LocalService localService;
    @Autowired
    private LocalRepo localRepo;

    public List<ConfirmedPayment> getConfirmedPayments() {
        return confirmedPaymentRepo.findAll();
    }
    public ConfirmedPayment getConfirmedPayment(Long id) {
        Optional<ConfirmedPayment> confirmedPayment = confirmedPaymentRepo.findById(id);
        if (confirmedPayment.isPresent()) {
            return confirmedPayment.get();
        }
        else throw new ResourceNotFoundException("ConfirmedPayment not found");
    }

    public ConfirmedPayment saveConfirmedPayment(ConfirmedPayment confirmedPayment) {
        Local l = localService.getById(confirmedPayment.getLocal().getId());
        Date date = confirmedPayment.getDate();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

        //get month and year
        int month = localDate.getMonthValue();
        int year = localDate.getYear();
        String periode = String.format("%02d/%d", month, year);
        confirmedPayment.setMois(month);
        confirmedPayment.setYear(year);
        confirmedPayment.setMoisAnnee(periode);
        confirmedPayment.setLocal(l);
        return confirmedPaymentRepo.save(confirmedPayment);
    }

    public void deleteConfirmedPayment(Long id) {
        confirmedPaymentRepo.deleteById(id);
    }

    public String ajouterEnMasse(List<ConfirmedPayment> confirmedPayment) {
        for (ConfirmedPayment c : confirmedPayment) {
            Date date = c.getDate();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            //get month and year
            int month = localDate.getMonthValue();
            int year = localDate.getYear();
            String periode = String.format("%02d/%d", month, year);
            if(confirmedPaymentRepo.findConfirmedPaymentByDateAndLocal(c.getLocal().getId() , periode) == null) {
                ConfirmedPayment a= saveConfirmedPayment(c);
            }
        }
        return "Ajouté avec succès";
    }

    public List<ConfirmedPayment> getConfirmedPaymentByLocal(Long id) {
        return confirmedPaymentRepo.findConfirmedPaymentByLocalId(id);
    }

    public void deleteConfirmedPaymentByLocalAndMonthAndYear(Long localId, int month, int year) {
        String moisAnnee = String.format("%02d/%d", month, year);
        ConfirmedPayment payment = confirmedPaymentRepo.findConfirmedPaymentByDateAndLocal(localId, moisAnnee);
        if (payment != null) {
            confirmedPaymentRepo.delete(payment);
        } else {
            throw new ResourceNotFoundException("Confirmed payment not found for the given local, month, and year");
        }
    }

}
