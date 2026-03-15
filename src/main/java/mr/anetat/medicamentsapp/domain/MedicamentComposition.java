package mr.anetat.medicamentsapp.domain;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "medicament_composition",
        uniqueConstraints = @UniqueConstraint(
                name = "uq_mc",
                columnNames = {"medicament_id", "molecule_id", "dosage_valeur", "unite_dosage_id"}
        )
)
@Getter
@Setter
@NoArgsConstructor
public class MedicamentComposition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicament_id", nullable = false)
    private Medicament medicament;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "molecule_id", nullable = false)
    private Molecule molecule;

    @Column(name = "dosage_valeur", nullable = false, precision = 10, scale = 2)
    private BigDecimal dosageValeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "unite_dosage_id", nullable = false)
    private UniteDosage uniteDosage;

    @Column(name = "ordre_affichage")
    private Integer ordreAffichage;
}

