package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.Avenant;
import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Entity.Paiement;
import ma.entraide.impot.Service.AvenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/Avenant")
@CrossOrigin("*")
public class AvenantController {
    @Autowired
    private AvenantService service;
    @Autowired
    private AvenantService avenantService;

    @GetMapping
    public ResponseEntity<List<Avenant>> getAvenants() {
        return ResponseEntity.ok(service.getAvenants());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Avenant> getAvenant(@PathVariable Long id) {
        try{
            Avenant av = service.getAvenant(id);
            return ResponseEntity.ok(av);
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Avenant> update(@PathVariable Long id, @RequestBody Avenant avenant){
        try{
            Avenant av = service.updateAvenant(id,avenant);
            return ResponseEntity.ok(av);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Avenant> addAvenant(@RequestBody Avenant avenant) {
        try {
            Avenant newAvenant = service.addAvenant(avenant);
            return ResponseEntity.ok(newAvenant);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        String r = service.deleteAvenant(id);
        return ResponseEntity.ok(r);
    }

    @PostMapping("/downloadEtat")
    public ResponseEntity<byte[]> downloadEtat(@RequestBody Avenant avenant) throws IOException {
        // Fetch payments based on the provided IDs

        byte[] result = avenantService.etatPdf(avenant);

        // Set headers for download
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "etatAvenant.pdf");
        headers.setContentLength(result.length);

        return ResponseEntity.ok()
                .headers(headers)
                .body(result);
    }
}


