package mr.anetat.medicamentsapp.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MedicamentAdminForm {

    @NotBlank
    @Size(max = 255)
    private String libelle;

    @NotBlank
    @Size(max = 500)
    private String libelleComplet;

    private Long formeId;

    @NotNull
    private Long laboratoireId;

    @Size(max = 255)
    private String presentation;

    @DecimalMin(value = "0.0")
    private BigDecimal prixPharmacie;

    @DecimalMin(value = "0.0")
    private BigDecimal prixGrossiste;

    @DecimalMin(value = "0.0")
    private BigDecimal prixCamec;

    @Size(max = 255)
    private String source;

    @Valid
    @NotNull
    @Size(min = 1)
    private List<MedicamentCompositionForm> compositions = new ArrayList<>();
}

