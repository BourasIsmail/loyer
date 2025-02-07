package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.Proprietaire;
import ma.entraide.impot.Service.ProprieteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/Proprietaire")
@CrossOrigin("*")
public class ProprieteController {

    @Autowired
    private ProprieteService proprieteService;

    @GetMapping("/all")
    public ResponseEntity<List<Proprietaire>> getAll() {
        List<Proprietaire> proprietaires =proprieteService.getAll();
        return ResponseEntity.ok(proprietaires);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Proprietaire> getById(@PathVariable Long id) {
        try{
            Proprietaire proprietaire = proprieteService.getById(id);
            return ResponseEntity.ok(proprietaire);
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/confirmedPaymentsReport/{id}/{year}")
    public ResponseEntity<byte[]> generateConfirmedPaymentsReport(@PathVariable Long id, @PathVariable int year) {
        try {
            byte[] pdfBytes = proprieteService.generateConfirmedPaymentsReport(id, year);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.setContentDispositionFormData("filename", "confirmed_payments_report_" + year + ".pdf");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Proprietaire> update(@PathVariable Long id, @RequestBody Proprietaire proprietaire) {
        try{
            Proprietaire proprietaireUpdate = proprieteService.update(id, proprietaire);
            return ResponseEntity.ok(proprietaireUpdate);
        }catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/add")
    public ResponseEntity<Proprietaire> create(@RequestBody Proprietaire proprietaire) {
        try {
            Proprietaire proprietaire1 = proprieteService.add(proprietaire);
            return ResponseEntity.ok(proprietaire1);
        }
        catch(Exception e){
            System.out.println(e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/byRegion/{id}")
    public ResponseEntity<List<Proprietaire>> getByRegion(@PathVariable Long id){
        return ResponseEntity.ok(proprieteService.proprietaireSearch(id));
    }

}
