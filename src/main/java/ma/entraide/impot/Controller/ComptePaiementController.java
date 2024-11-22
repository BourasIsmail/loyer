package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.ComptePayement;
import ma.entraide.impot.Entity.Paiement;
import ma.entraide.impot.Service.ComptePayementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Compte")
@CrossOrigin("*")
public class ComptePaiementController {
    @Autowired
    private ComptePayementService comptePayementService;

    @GetMapping
    public ResponseEntity<List<ComptePayement>> all() {
        List<ComptePayement> compte =comptePayementService.findAll();
        return ResponseEntity.ok(compte);
    }
}
