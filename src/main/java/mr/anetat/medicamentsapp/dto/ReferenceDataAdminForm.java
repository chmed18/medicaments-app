package mr.anetat.medicamentsapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ReferenceDataAdminForm {

    @NotBlank(message = "Le libellé principal est obligatoire.")
    @Size(max = 255, message = "Le libellé principal ne doit pas dépasser 255 caractères.")
    private String primaryValue;

    @Size(max = 500, message = "La valeur complémentaire ne doit pas dépasser 500 caractères.")
    private String secondaryValue;
}

