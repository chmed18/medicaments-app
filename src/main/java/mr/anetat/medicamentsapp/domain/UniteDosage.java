package mr.anetat.medicamentsapp.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "unite_dosage")
@Getter
@Setter
@NoArgsConstructor
public class UniteDosage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "libelle", length = 100)
    private String libelle;

    @Column(name = "libelle_complet", length = 255)
    private String libelleComplet;
}


