package mr.anetat.medicamentsapp.repository;

import java.util.List;
import java.util.Optional;

import mr.anetat.medicamentsapp.domain.Medicament;
import mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    long countByForme_Id(Long formeId);

    long countByLaboratoire_Id(Long laboratoireId);

    @Query("""
            SELECT new mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto(
                m.id,
                m.libelleComplet,
                COALESCE(f.libelleComplet, f.libelle),
                l.nom,
                m.presentation,
                m.prixPharmacie,
                COUNT(mc.id)
            )
            FROM Medicament m
            LEFT JOIN m.forme f
            LEFT JOIN m.laboratoire l
            LEFT JOIN MedicamentComposition mc ON mc.medicament.id = m.id
            GROUP BY m.id, m.libelleComplet, f.libelleComplet, f.libelle, l.nom, m.presentation, m.prixPharmacie
            ORDER BY LOWER(m.libelleComplet)
            """)
    List<MedicamentAdminListItemDto> findAllForAdminList();

    @Query(value = """
            SELECT new mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto(
                m.id,
                m.libelleComplet,
                COALESCE(f.libelleComplet, f.libelle),
                l.nom,
                m.presentation,
                m.prixPharmacie,
                COUNT(mc.id)
            )
            FROM Medicament m
            LEFT JOIN m.forme f
            LEFT JOIN m.laboratoire l
            LEFT JOIN MedicamentComposition mc ON mc.medicament.id = m.id
            GROUP BY m.id, m.libelleComplet, f.libelleComplet, f.libelle, l.nom, m.presentation, m.prixPharmacie
            ORDER BY LOWER(m.libelleComplet)
            """,
            countQuery = "SELECT COUNT(m.id) FROM Medicament m")
    Page<MedicamentAdminListItemDto> findAllForAdminPage(Pageable pageable);

    @Query(value = """
            SELECT new mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto(
                m.id,
                m.libelleComplet,
                COALESCE(f.libelleComplet, f.libelle),
                l.nom,
                m.presentation,
                m.prixPharmacie,
                COUNT(mc.id)
            )
            FROM Medicament m
            LEFT JOIN m.forme f
            LEFT JOIN m.laboratoire l
            LEFT JOIN MedicamentComposition mc ON mc.medicament.id = m.id
            WHERE LOWER(m.libelleComplet) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(m.libelle) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :query, '%'))
            GROUP BY m.id, m.libelleComplet, f.libelleComplet, f.libelle, l.nom, m.presentation, m.prixPharmacie
            ORDER BY LOWER(m.libelleComplet)
            """,
            countQuery = """
            SELECT COUNT(DISTINCT m.id)
            FROM Medicament m
            LEFT JOIN m.laboratoire l
            WHERE LOWER(m.libelleComplet) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(m.libelle) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(l.nom) LIKE LOWER(CONCAT('%', :query, '%'))
            """)
    Page<MedicamentAdminListItemDto> findForAdminListByQuery(@Param("query") String query, Pageable pageable);

    @Query("SELECT m FROM Medicament m WHERE LOWER(m.libelle) LIKE LOWER(CONCAT(:query, '%')) OR LOWER(m.libelleComplet) LIKE LOWER(CONCAT(:query, '%'))")
    List<Medicament> searchByLabel(@Param("query") String query);

    @Query("SELECT m.libelleComplet FROM Medicament m WHERE LOWER(m.libelleComplet) LIKE LOWER(CONCAT(:query, '%')) ORDER BY m.libelleComplet")
    List<String> findLibelleCompletSuggestions(@Param("query") String query, Pageable pageable);

    @Query("""
            SELECT m
            FROM Medicament m
            LEFT JOIN FETCH m.forme
            LEFT JOIN FETCH m.laboratoire
            LEFT JOIN FETCH m.groupeEquivalence
            WHERE m.id = :id
            """)
    Optional<Medicament> findDetailById(@Param("id") Long id);

    @Query("""
            SELECT m
            FROM Medicament m
            LEFT JOIN FETCH m.laboratoire
            WHERE m.groupeEquivalence.id = :groupeId
              AND m.id <> :currentMedicamentId
            ORDER BY LOWER(m.libelleComplet)
            """)
    List<Medicament> findEquivalentMedicaments(
            @Param("groupeId") Long groupeId,
            @Param("currentMedicamentId") Long currentMedicamentId);

    List<Medicament> findByGroupeEquivalenceId(Long groupeId);
}

