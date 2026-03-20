package mr.anetat.medicamentsapp.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentCompositionForm {

    @NotNull
    private Long moleculeId;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal dosageValeur;

    @NotNull
    private Long uniteDosageId;

    private Integer ordreAffichage;
}

