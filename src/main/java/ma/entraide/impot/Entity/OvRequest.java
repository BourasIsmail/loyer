package ma.entraide.impot.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OvRequest {
    private List<Local> locals;
    private Date date;

    @JsonProperty("nOrdre")
    private String nOrdre;

    @JsonProperty("nOP")
    private String nOP;

    @JsonProperty("dateCreation")
    private String dateCreation;

    @JsonProperty("comptePaiement")
    private ComptePayement comptePaiement;

    @JsonProperty("mode")
    private String mode;
}
