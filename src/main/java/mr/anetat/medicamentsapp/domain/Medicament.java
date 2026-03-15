package mr.anetat.medicamentsapp.domain;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forme_id", nullable = false)
    private Forme forme;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "titulaire_amm_id")
    private TitulaireAmm titulaireAmm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "groupe_equivalence_id")
    private GroupeEquivalence groupeEquivalence;

    @Column(name = "source", length = 255)
    private String source;
}

