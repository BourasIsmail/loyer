package ma.entraide.impot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ConfirmedPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date date;

    private int mois;

    private int year;

    private String moisAnnee;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "local_id")
    private Local local;

}
