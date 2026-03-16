package mr.anetat.medicamentsapp.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentSearchResultDto {

    private Long id;
    private String libelle;
    private String libelleComplet;
    private String presentation;
    private BigDecimal prixPharmacie;
    private BigDecimal prixGrossiste;
    private BigDecimal prixCamec;
}

