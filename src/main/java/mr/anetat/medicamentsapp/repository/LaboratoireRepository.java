package mr.anetat.medicamentsapp.repository;

import mr.anetat.medicamentsapp.domain.Laboratoire;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LaboratoireRepository extends JpaRepository<Laboratoire, Long> {

    boolean existsByNomIgnoreCase(String nom);

    boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);

	Page<Laboratoire> findByNomContainingIgnoreCaseOrAdresseContainingIgnoreCase(
			String nom,
			String adresse,
			Pageable pageable);
}


