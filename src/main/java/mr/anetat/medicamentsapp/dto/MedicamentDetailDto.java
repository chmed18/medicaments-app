package mr.anetat.medicamentsapp.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentDetailDto {

    private Long id;
    private String libelle;
    private String libelleComplet;
    private String forme;
    private String presentation;
    private String laboratoire;
    private BigDecimal prixPharmacie;
    private List<MoleculeDto> molecules;
    private List<EquivalentDto> equivalents;
}

