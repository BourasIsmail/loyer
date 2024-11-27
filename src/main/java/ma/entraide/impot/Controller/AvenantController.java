package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.Avenant;
import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Service.AvenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Avenant")
@CrossOrigin("*")
public class AvenantController {
    @Autowired
    private AvenantService service;

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

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        String r = service.deleteAvenant(id);
        return ResponseEntity.ok(r);
    }
}

