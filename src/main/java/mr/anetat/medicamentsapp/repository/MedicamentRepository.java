package mr.anetat.medicamentsapp.repository;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Medicament;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    @Query("SELECT m FROM Medicament m WHERE LOWER(m.libelle) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(m.libelleComplet) LIKE LOWER(CONCAT(:query, '%'))")
    List<Medicament> searchByLabel(@Param("query") String query);

    @Query("SELECT m.libelleComplet FROM Medicament m WHERE LOWER(m.libelleComplet) LIKE LOWER(CONCAT(:query, '%')) ORDER BY m.libelleComplet")
    List<String> findLibelleCompletSuggestions(@Param("query") String query, Pageable pageable);

    List<Medicament> findByGroupeEquivalenceId(Long groupeId);
}

