package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.Local;
import ma.entraide.impot.Service.LocalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE;

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

    @PutMapping(value = "/upload/{id}", consumes = {MULTIPART_FORM_DATA_VALUE})
    public String upload(@PathVariable Long id, @RequestParam(required = false) MultipartFile file){
        if(file != null){
            localService.uploadContact(id, file);
            return "uploaded";
        }
        else {
            return "error";
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id){
        Local local = localService.getById(id);
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(local.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + local.getFileName() + "\"")
                .body(new ByteArrayResource(local.getContrat()));
    }


    @GetMapping("/dashboard")
    public ResponseEntity<Object> dashboard(){
        Object data = localService.dashboard();
        return ResponseEntity.ok(data);
    }


}
