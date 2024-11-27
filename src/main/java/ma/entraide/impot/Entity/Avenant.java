package ma.entraide.impot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Avenant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "local_id")
    private Local local;

    private double montant;

    private String etat;

    public Avenant(Local local, double total) {
        this.local = local;
        this.montant = total;
    }
}
