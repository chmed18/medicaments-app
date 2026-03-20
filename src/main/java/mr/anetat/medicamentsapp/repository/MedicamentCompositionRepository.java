package mr.anetat.medicamentsapp.repository;

import java.util.List;

import mr.anetat.medicamentsapp.domain.MedicamentComposition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicamentCompositionRepository extends JpaRepository<MedicamentComposition, Long> {

    List<MedicamentComposition> findByMedicament_Id(Long medicamentId);

    void deleteByMedicament_Id(Long medicamentId);

	@Query("""
			SELECT mc
			FROM MedicamentComposition mc
			JOIN FETCH mc.molecule
			JOIN FETCH mc.uniteDosage
			WHERE mc.medicament.id = :medicamentId
			ORDER BY CASE WHEN mc.ordreAffichage IS NULL THEN 1 ELSE 0 END,
					 mc.ordreAffichage,
					 LOWER(mc.molecule.nom)
			""")
	List<MedicamentComposition> findDetailedByMedicamentId(@Param("medicamentId") Long medicamentId);
}

