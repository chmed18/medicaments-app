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
public class EquivalentDto {

    private Long id;
    private String libelleComplet;
    private String laboratoire;
    private String presentation;
    private BigDecimal prixPharmacie;
}

