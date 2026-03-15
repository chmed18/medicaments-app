package mr.anetat.medicamentsapp.repository;

import mr.anetat.medicamentsapp.domain.MedicamentComposition;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MedicamentCompositionRepository extends JpaRepository<MedicamentComposition, Long> {
}

