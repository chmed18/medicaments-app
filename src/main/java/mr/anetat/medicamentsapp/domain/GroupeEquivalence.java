package mr.anetat.medicamentsapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "groupe_equivalence",
        uniqueConstraints = @UniqueConstraint(name = "uq_groupe_equivalence", columnNames = "signature")
)
@Getter
@Setter
@NoArgsConstructor
public class GroupeEquivalence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "signature", nullable = false, length = 500)
    private String signature;

    @Column(name = "libelle", length = 255)
    private String libelle;
}

