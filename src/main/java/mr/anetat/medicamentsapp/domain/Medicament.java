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
@Table(name = "medicament")
@Getter
@Setter
@NoArgsConstructor
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "libelle", nullable = false, length = 255)
    private String libelle;

    @Column(name = "libelle_complet", nullable = false, length = 500)
    private String libelleComplet;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "forme_id", nullable = true)
    private Forme forme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "laboratoire_id")
    private Laboratoire laboratoire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_equivalence_id")
    private GroupeEquivalence groupeEquivalence;

    @Column(name = "presentation", length = 255)
    private String presentation;

    @Column(name = "prix_pharmacie", precision = 10, scale = 2)
    private BigDecimal prixPharmacie;

    @Column(name = "prix_grossiste", precision = 10, scale = 2)
    private BigDecimal prixGrossiste;

    @Column(name = "prix_camec", precision = 10, scale = 2)
    private BigDecimal prixCamec;

    @Column(name = "source", length = 255)
    private String source;
}

