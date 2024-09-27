package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.Proprietaire;
import ma.entraide.impot.Service.ProprieteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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


}
