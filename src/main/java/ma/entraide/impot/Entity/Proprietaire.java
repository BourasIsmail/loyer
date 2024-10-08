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


    private String type;

    private String adresse;

    @OneToMany(cascade ={CascadeType.DETACH} )
    private List<Rib> rib;


    @ManyToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "province_id")
    private Province province;
}
