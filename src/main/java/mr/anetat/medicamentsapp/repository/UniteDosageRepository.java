package mr.anetat.medicamentsapp.repository;

import mr.anetat.medicamentsapp.domain.UniteDosage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UniteDosageRepository extends JpaRepository<UniteDosage, Long> {

	boolean existsByLibelleIgnoreCase(String libelle);

	boolean existsByLibelleIgnoreCaseAndIdNot(String libelle, Long id);

	Page<UniteDosage> findByLibelleContainingIgnoreCaseOrLibelleCompletContainingIgnoreCase(
			String libelle,
			String libelleComplet,
			Pageable pageable);
}

