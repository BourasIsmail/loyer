package ma.entraide.impot.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.Map;
import java.util.HashMap;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class RASConfig {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private double lowerThreshold1;
    private double upperThreshold1;
    private int percentage1;

    private double lowerThreshold2;
    private double upperThreshold2;
    private int percentage2;

    private int percentage3;

    @ElementCollection
    @CollectionTable(name = "special_case_percentages", joinColumns = @JoinColumn(name = "ras_config_id"))
    @MapKeyColumn(name = "local_id")
    @Column(name = "percentage")
    private Map<Long, Integer> specialCasePercentages = new HashMap<>();
}

