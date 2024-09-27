package ma.entraide.impot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Proprietaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "owner_id")
    private Long id;


    private String nomComplet;

    @Column(unique = true)
    private String cin;

    private String telephone;

    private String etat;

    private String type;

    private String adresse;

    @ManyToMany(cascade ={
            CascadeType.DETACH,
            CascadeType.MERGE,
            CascadeType.REFRESH,
            CascadeType.PERSIST,
    } )
    @JoinTable(
            name = "proprietaire_rib",
            joinColumns = @JoinColumn(name = "proprietaire_id"),
            inverseJoinColumns = @JoinColumn(name = "rib"))
    private List<Rib> rib;


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "province_id")
    private Province province;
}
