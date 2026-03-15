package mr.anetat.medicamentsapp.repository;

import mr.anetat.medicamentsapp.domain.Molecule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MoleculeRepository extends JpaRepository<Molecule, Long> {
}

