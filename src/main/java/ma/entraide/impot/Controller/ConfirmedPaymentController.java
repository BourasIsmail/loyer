package ma.entraide.impot.Controller;

import ma.entraide.impot.Entity.ConfirmedPayment;
import ma.entraide.impot.Service.ConfirmedPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/Confirme")
@CrossOrigin("*")
public class ConfirmedPaymentController {
    @Autowired
    private ConfirmedPaymentService confirmedPaymentService;

    @GetMapping
    public ResponseEntity<List<ConfirmedPayment>> getConfirmedPayments() {
        return ResponseEntity.ok(confirmedPaymentService.getConfirmedPayments());
    }

    @PostMapping
    public ResponseEntity<String> addConfirmedPayment(@RequestBody List<ConfirmedPayment> confirmedPayment) {
        try{
            String result = confirmedPaymentService.ajouterEnMasse(confirmedPayment);
            return ResponseEntity.ok(result);
        }catch (Exception e){

            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteConfirmedPayment(@PathVariable Long id) {
        confirmedPaymentService.deleteConfirmedPayment(id);
        return ResponseEntity.ok("Confirmed payment deleted");
    }

    @DeleteMapping("/local/{localId}/month/{month}/year/{year}")
    public ResponseEntity<String> deleteConfirmedPaymentByLocalAndMonthAndYear(
            @PathVariable Long localId,
            @PathVariable int month,
            @PathVariable int year) {
        try {
            confirmedPaymentService.deleteConfirmedPaymentByLocalAndMonthAndYear(localId, month, year);
            return ResponseEntity.ok("Confirmed payment deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
