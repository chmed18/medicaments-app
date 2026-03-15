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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "medicament_presentation")
@Getter
@Setter
@NoArgsConstructor
public class MedicamentPresentation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medicament_id", nullable = false)
    private Medicament medicament;

    @Column(name = "presentation", nullable = false, length = 255)
    private String presentation;

    @Column(name = "prix_pharmacie", precision = 10, scale = 2)
    private BigDecimal prixPharmacie;

    @Column(name = "prix_grossiste", precision = 10, scale = 2)
    private BigDecimal prixGrossiste;

    @Column(name = "prix_camec", precision = 10, scale = 2)
    private BigDecimal prixCamec;

    @Column(name = "code_barre", unique = true, length = 100)
    private String codeBarre;
}

