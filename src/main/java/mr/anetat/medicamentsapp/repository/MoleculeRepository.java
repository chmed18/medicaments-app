package mr.anetat.medicamentsapp.repository;

import mr.anetat.medicamentsapp.domain.Molecule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoleculeRepository extends JpaRepository<Molecule, Long> {

	boolean existsByNomIgnoreCase(String nom);

	boolean existsByNomIgnoreCaseAndIdNot(String nom, Long id);

	Page<Molecule> findByNomContainingIgnoreCase(String nom, Pageable pageable);
}

