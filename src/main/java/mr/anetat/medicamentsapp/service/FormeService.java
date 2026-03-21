package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.FormeRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormeService {

    private final FormeRepository formeRepository;
    private final MedicamentRepository medicamentRepository;

    public FormeService(FormeRepository formeRepository, MedicamentRepository medicamentRepository) {
        this.formeRepository = formeRepository;
        this.medicamentRepository = medicamentRepository;
    }

    public List<Forme> findAll() {
        return formeRepository.findAll(Sort.by(Sort.Direction.ASC, "libelle"));
    }

    @Transactional(readOnly = true)
    public Page<Forme> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return formeRepository.findAll(pageable);
        }
        String normalized = query.trim();
        return formeRepository.findByLibelleContainingIgnoreCaseOrLibelleCompletContainingIgnoreCase(
                normalized,
                normalized,
                pageable);
    }

    public Forme findById(Long id) {
        return formeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forme not found with id: " + id));
    }

    public Forme create(Forme forme) {
        forme.setId(null);
        forme.setLibelle(normalizeRequiredValue(forme.getLibelle()));
        forme.setLibelleComplet(trimToNull(forme.getLibelleComplet()));
        ensureUniqueLibelle(forme.getLibelle(), null);
        return saveWithDuplicateHandling(forme);
    }

    public Forme update(Long id, Forme forme) {
        Forme existingForme = findById(id);
        existingForme.setLibelle(normalizeRequiredValue(forme.getLibelle()));
        existingForme.setLibelleComplet(trimToNull(forme.getLibelleComplet()));
        ensureUniqueLibelle(existingForme.getLibelle(), id);
        return saveWithDuplicateHandling(existingForme);
    }

    public void delete(Long id) {
        findById(id);
        long usageCount = medicamentRepository.countByForme_Id(id);
        if (usageCount > 0) {
            throw new ReferenceDataInUseException(
                    "Suppression impossible : cette forme est utilisee par " + usageCount + " medicament(s).");
        }
        formeRepository.deleteById(id);
    }

    private void ensureUniqueLibelle(String libelle, Long currentId) {
        boolean exists = currentId == null
                ? formeRepository.existsByLibelleIgnoreCase(libelle)
                : formeRepository.existsByLibelleIgnoreCaseAndIdNot(libelle, currentId);

        if (exists) {
            throw new ReferenceDataDuplicateException(
                    "Une forme avec le libellé \"" + libelle + "\" existe déjà."
            );
        }
    }

    private Forme saveWithDuplicateHandling(Forme forme) {
        try {
            return formeRepository.save(forme);
        } catch (DataIntegrityViolationException ex) {
            throw new ReferenceDataDuplicateException(
                    "Une forme avec le libellé \"" + forme.getLibelle() + "\" existe déjà."
            );
        }
    }

    private String normalizeRequiredValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le libellé de la forme est obligatoire.");
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

