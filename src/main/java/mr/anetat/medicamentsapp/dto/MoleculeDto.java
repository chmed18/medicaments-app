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
public class MoleculeDto {

    private String nom;
    private BigDecimal dosageValeur;
    private String uniteDosage;
}

