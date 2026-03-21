package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.UniteDosageRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UniteDosageService {

    private final UniteDosageRepository uniteDosageRepository;
    private final MedicamentCompositionRepository medicamentCompositionRepository;

    public UniteDosageService(
            UniteDosageRepository uniteDosageRepository,
            MedicamentCompositionRepository medicamentCompositionRepository) {
        this.uniteDosageRepository = uniteDosageRepository;
        this.medicamentCompositionRepository = medicamentCompositionRepository;
    }

    public List<UniteDosage> findAll() {
        return uniteDosageRepository.findAll(Sort.by(Sort.Direction.ASC, "libelle"));
    }

    @Transactional(readOnly = true)
    public Page<UniteDosage> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return uniteDosageRepository.findAll(pageable);
        }
        String normalized = query.trim();
        return uniteDosageRepository.findByLibelleContainingIgnoreCaseOrLibelleCompletContainingIgnoreCase(
                normalized,
                normalized,
                pageable);
    }

    public UniteDosage findById(Long id) {
        return uniteDosageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UniteDosage not found with id: " + id));
    }

    public UniteDosage create(UniteDosage uniteDosage) {
        uniteDosage.setId(null);
        uniteDosage.setLibelle(normalizeRequiredValue(uniteDosage.getLibelle()));
        uniteDosage.setLibelleComplet(trimToNull(uniteDosage.getLibelleComplet()));
        ensureUniqueLibelle(uniteDosage.getLibelle(), null);
        return saveWithDuplicateHandling(uniteDosage);
    }

    public UniteDosage update(Long id, UniteDosage uniteDosage) {
        UniteDosage existingUniteDosage = findById(id);
        existingUniteDosage.setLibelle(normalizeRequiredValue(uniteDosage.getLibelle()));
        existingUniteDosage.setLibelleComplet(trimToNull(uniteDosage.getLibelleComplet()));
        ensureUniqueLibelle(existingUniteDosage.getLibelle(), id);
        return saveWithDuplicateHandling(existingUniteDosage);
    }

    public void delete(Long id) {
        findById(id);
        long usageCount = medicamentCompositionRepository.countByUniteDosage_Id(id);
        if (usageCount > 0) {
            throw new ReferenceDataInUseException(
                    "Suppression impossible : cette unite de dosage est utilisee dans " + usageCount + " composition(s).");
        }
        uniteDosageRepository.deleteById(id);
    }

    private void ensureUniqueLibelle(String libelle, Long currentId) {
        boolean exists = currentId == null
                ? uniteDosageRepository.existsByLibelleIgnoreCase(libelle)
                : uniteDosageRepository.existsByLibelleIgnoreCaseAndIdNot(libelle, currentId);

        if (exists) {
            throw new ReferenceDataDuplicateException(
                    "Une unité de dosage avec le libellé \"" + libelle + "\" existe déjà."
            );
        }
    }

    private UniteDosage saveWithDuplicateHandling(UniteDosage uniteDosage) {
        try {
            return uniteDosageRepository.save(uniteDosage);
        } catch (DataIntegrityViolationException ex) {
            throw new ReferenceDataDuplicateException(
                    "Une unité de dosage avec le libellé \"" + uniteDosage.getLibelle() + "\" existe déjà."
            );
        }
    }

    private String normalizeRequiredValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le libellé de l'unité de dosage est obligatoire.");
        }
        return value.trim();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}

