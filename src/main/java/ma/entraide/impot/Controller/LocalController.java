package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/local")
@CrossOrigin("*")
public class LocalController {
    @Autowired
    private LocalService localService;


    @GetMapping("/all")
    public ResponseEntity<List<Local>> getAll() {
        List<Local> locals =localService.getAll();
        return ResponseEntity.ok(locals);
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Local> getById(@PathVariable Long id) {
        try{
            Local local = localService.getById(id);
            return ResponseEntity.ok(local);
        }
        catch(Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getByRegion/{id}")
    public ResponseEntity<List<Local>> getByRegion(@PathVariable Long id){
        List<Local> locaux = localService.getLocalByRegion(id);
        return ResponseEntity.ok(locaux);
    }

    @GetMapping("/getByDelegation/{id}")
    public ResponseEntity<List<Local>> getByDelegation(@PathVariable Long id){
        List<Local> locaux = localService.getLocalByDelegation(id);
        return ResponseEntity.ok(locaux);
    }

    @GetMapping("/getByProprietaire/{id}")
    public ResponseEntity<List<Local>> getByProprietaire(@PathVariable Long id){
        List<Local> locaux = localService.getLocalByProprietaires(id);
        return ResponseEntity.ok(locaux);
    }

    @PostMapping("/add")
    public ResponseEntity<Local> add(@RequestBody Local local){
        try {
            Local newLocal =localService.addLocal(local);
            return ResponseEntity.ok(newLocal);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Local> update(@PathVariable Long id, @RequestBody Local local){
        try {
            Local result = localService.updateLocal(id, local);
            return ResponseEntity.ok(result);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id){
        try {
            String result = localService.deleteLocal(id);
            return ResponseEntity.ok(result);
        }
        catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }



}
