package mr.anetat.medicamentsapp.repository;

import mr.anetat.medicamentsapp.domain.Forme;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FormeRepository extends JpaRepository<Forme, Long> {

    boolean existsByLibelleIgnoreCase(String libelle);

    boolean existsByLibelleIgnoreCaseAndIdNot(String libelle, Long id);

	Page<Forme> findByLibelleContainingIgnoreCaseOrLibelleCompletContainingIgnoreCase(
			String libelle,
			String libelleComplet,
			Pageable pageable);
}

