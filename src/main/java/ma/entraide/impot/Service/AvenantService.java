package ma.entraide.impot.Service;

import ma.entraide.impot.Entity.Avenant;
import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Paiement;
import ma.entraide.impot.Repository.AvenantsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static ma.entraide.impot.Service.PdfGenerator.generatePdfEtat;
import static ma.entraide.impot.Service.PdfGenerator.generatePdfEtatAvenant;

@Service
public class AvenantService {
    @Autowired
    private AvenantsRepo avenantsRepo;
    @Autowired
    private LocalService localService;

    public List<Avenant> getAvenants() {
        return avenantsRepo.findAll();
    }

    public Avenant addAvenant(Avenant avenant) {
        Local l = localService.getById(avenant.getLocal().getId());
        avenant.setLocal(l);
        avenant.setEtat("En Attente");
        return avenantsRepo.save(avenant);
    }

    public Avenant getAvenant(Long id) {
        Optional<Avenant> avenant = avenantsRepo.findById(id);
        if (avenant.isPresent()) {
            return avenant.get();
        }
        else{
            throw new ResourceNotFoundException("Avenant " + id + " n'existe pas");
        }
    }

    public Avenant updateAvenant(Long id, Avenant avenant) {
        Avenant a = getAvenant(id);
        Local l = localService.getById(avenant.getLocal().getId());
        a.setLocal(l);
        a.setEtat(avenant.getEtat());
        a.setMontant(avenant.getMontant());
        return avenantsRepo.save(a);
    }

    public String deleteAvenant(Long id) {
        Avenant a = getAvenant(id);
        avenantsRepo.delete(a);
        return "deleted";
    }

    public byte[] etatPdf(Avenant avenant) throws IOException {
        return generatePdfEtatAvenant(avenant);
    }

}
