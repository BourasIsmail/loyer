package ma.entraide.impot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToMany(cascade ={
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
    } )
    @JoinTable(
            name = "immeuble_proprietaire",
            joinColumns = @JoinColumn(name = "immeuble_id"),
            inverseJoinColumns = @JoinColumn(name = "proprietaire_id"))
    private List<Proprietaire> proprietaires;

    private String adresse;

    private float brutMensuel;

    @Column(length = 24)
    private String rib;

    private String etat;

    @Lob
    @Column(columnDefinition = "longblob")
    private byte[] contrat;

    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "province_id")
    private Province province;


}
