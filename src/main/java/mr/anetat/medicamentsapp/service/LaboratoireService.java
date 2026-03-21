package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.LaboratoireRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LaboratoireService {

    private final LaboratoireRepository laboratoireRepository;
    private final MedicamentRepository medicamentRepository;

    public LaboratoireService(LaboratoireRepository laboratoireRepository, MedicamentRepository medicamentRepository) {
        this.laboratoireRepository = laboratoireRepository;
        this.medicamentRepository = medicamentRepository;
    }

    public List<Laboratoire> findAll() {
        return laboratoireRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
    }

    @Transactional(readOnly = true)
    public Page<Laboratoire> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return laboratoireRepository.findAll(pageable);
        }
        String normalized = query.trim();
        return laboratoireRepository.findByNomContainingIgnoreCaseOrAdresseContainingIgnoreCase(
                normalized,
                normalized,
                pageable);
    }

    public Laboratoire findById(Long id) {
        return laboratoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratoire not found with id: " + id));
    }

    public Laboratoire create(Laboratoire laboratoire) {
        laboratoire.setId(null);
        laboratoire.setNom(normalizeRequiredValue(laboratoire.getNom()));
        laboratoire.setAdresse(trimToNull(laboratoire.getAdresse()));
        ensureUniqueNom(laboratoire.getNom(), null);
        return saveWithDuplicateHandling(laboratoire);
    }

    public Laboratoire update(Long id, Laboratoire laboratoire) {
        Laboratoire existingLaboratoire = findById(id);
        existingLaboratoire.setNom(normalizeRequiredValue(laboratoire.getNom()));
        existingLaboratoire.setAdresse(trimToNull(laboratoire.getAdresse()));
        ensureUniqueNom(existingLaboratoire.getNom(), id);
        return saveWithDuplicateHandling(existingLaboratoire);
    }

    public void delete(Long id) {
        findById(id);
        long usageCount = medicamentRepository.countByLaboratoire_Id(id);
        if (usageCount > 0) {
            throw new ReferenceDataInUseException(
                    "Suppression impossible : ce laboratoire est utilise par " + usageCount + " medicament(s).");
        }
        laboratoireRepository.deleteById(id);
    }

    private void ensureUniqueNom(String nom, Long currentId) {
        boolean exists = currentId == null
                ? laboratoireRepository.existsByNomIgnoreCase(nom)
                : laboratoireRepository.existsByNomIgnoreCaseAndIdNot(nom, currentId);

        if (exists) {
            throw new ReferenceDataDuplicateException(
                    "Un laboratoire avec le nom \"" + nom + "\" existe déjà."
            );
        }
    }

    private Laboratoire saveWithDuplicateHandling(Laboratoire laboratoire) {
        try {
            return laboratoireRepository.save(laboratoire);
        } catch (DataIntegrityViolationException ex) {
            throw new ReferenceDataDuplicateException(
                    "Un laboratoire avec le nom \"" + laboratoire.getNom() + "\" existe déjà."
            );
        }
    }

    private String normalizeRequiredValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le nom du laboratoire est obligatoire.");
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

