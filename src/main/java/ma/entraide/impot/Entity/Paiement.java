package ma.entraide.impot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Month;
import java.time.Year;
import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Paiement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //input
    private Date dateCreation;

    private int month;

    private int year;

    private String moisAnnee;

    private double bruteMensuel;

    private int pourcentageRAS;

    private double ras;

    private double netMensuel;

    private String mtNetEnLettre;

    private String rib;

    //input
    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "local_id")
    private Local local;

    public Paiement(Date date, int month, int year, String periode, String rib, double bruteMensuel, int pourcentageRAS, double ras, double netMensuel, Local local) {
        this.dateCreation = date;
        this.month = month;
        this.year = year;
        this.rib= rib;
        this.moisAnnee = periode;
        this.bruteMensuel = bruteMensuel;
        this.pourcentageRAS = pourcentageRAS;
        this.ras = ras;
        this.netMensuel = netMensuel;
        this.local = local;
    }

}
