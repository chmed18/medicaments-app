package mr.anetat.medicamentsapp.dto;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MedicamentAdminListItemDto {

    private Long id;
    private String libelleComplet;
    private String forme;
    private String laboratoire;
    private String presentation;
    private BigDecimal prixPharmacie;
    private Integer nombreMolecules;

    public MedicamentAdminListItemDto(
            Long id,
            String libelleComplet,
            String forme,
            String laboratoire,
            String presentation,
            BigDecimal prixPharmacie,
            Long nombreMolecules) {
        this.id = id;
        this.libelleComplet = libelleComplet;
        this.forme = forme;
        this.laboratoire = laboratoire;
        this.presentation = presentation;
        this.prixPharmacie = prixPharmacie;
        this.nombreMolecules = nombreMolecules == null ? 0 : nombreMolecules.intValue();
    }
}


