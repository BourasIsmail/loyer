package ma.entraide.impot.Controller;

import lombok.Data;
import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.OvRequest;
import ma.entraide.impot.Entity.Paiement;
import ma.entraide.impot.Entity.PayerRequest;
import ma.entraide.impot.Service.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/Paiement")
@CrossOrigin("*")
public class PaiementController {
    @Autowired
    private PaiementService paiementService;

    @GetMapping("/byLocal/{id}")
    public ResponseEntity<List<Paiement>> byLocal(@PathVariable Long id) {
        List<Paiement> locals =paiementService.getByLocal(id);
        return ResponseEntity.ok(locals);
    }

    @GetMapping("/byId/{id}")
    public ResponseEntity<Paiement> byId(@PathVariable Long id) {
        try{
            Paiement local = paiementService.getById(id);
            return ResponseEntity.ok(local);
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Paiement> add(@RequestBody Paiement paiement) {
        try {
            Paiement result = paiementService.save(paiement);
            return ResponseEntity.ok(result);
        }catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    @PostMapping("/downloadEtat")
    public ResponseEntity<byte[]> downloadEtat(@RequestBody List<Paiement> paiements) throws IOException {
        // Fetch payments based on the provided IDs

        byte[] result = paiementService.etatPdf(paiements);

        // Set headers for download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "etat.pdf");
        headers.setContentLength(result.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }

    @PostMapping("/paiements")
    public ResponseEntity<byte[]> payer(@RequestBody PayerRequest request)  {
        try {
            byte[] result = paiementService.payerEnMasse(request.getLocals(), request.getDate());

            // Set headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "etat.pdf");
            headers.setContentLength(result.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(result);
        }
        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }

    }

    @PostMapping("/ov")
    public ResponseEntity<byte[]> ov(@RequestBody OvRequest request)  {
        try {
            System.out.println(request);
            byte[] result = paiementService.genOv(request.getLocals(), request.getDate(), request.getNOrdre(),
                    request.getNOP(), request.getDateCreation(), request.getComptePaiement(), request.getMode());

            // Set headers for download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("attachment", "ov.pdf");
            headers.setContentLength(result.length);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(result);
        }
        catch (Exception e) {
            System.out.println(e);
            return ResponseEntity.notFound().build();
        }

    }

    @GetMapping("/all")
    public ResponseEntity<List<Paiement>> all() {
        return ResponseEntity.ok(paiementService.getAll());
    }


}
