package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.MoleculeRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MoleculeService {

    private final MoleculeRepository moleculeRepository;
    private final MedicamentCompositionRepository medicamentCompositionRepository;

    public MoleculeService(
            MoleculeRepository moleculeRepository,
            MedicamentCompositionRepository medicamentCompositionRepository) {
        this.moleculeRepository = moleculeRepository;
        this.medicamentCompositionRepository = medicamentCompositionRepository;
    }

    public List<Molecule> findAll() {
        return moleculeRepository.findAll(Sort.by(Sort.Direction.ASC, "nom"));
    }

    @Transactional(readOnly = true)
    public Page<Molecule> search(String query, Pageable pageable) {
        if (query == null || query.isBlank()) {
            return moleculeRepository.findAll(pageable);
        }
        return moleculeRepository.findByNomContainingIgnoreCase(query.trim(), pageable);
    }

    public Molecule findById(Long id) {
        return moleculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Molecule not found with id: " + id));
    }

    public Molecule create(Molecule molecule) {
        molecule.setId(null);
        molecule.setNom(normalizeRequiredValue(molecule.getNom()));
        ensureUniqueNom(molecule.getNom(), null);
        return saveWithDuplicateHandling(molecule);
    }

    public Molecule update(Long id, Molecule molecule) {
        Molecule existingMolecule = findById(id);
        existingMolecule.setNom(normalizeRequiredValue(molecule.getNom()));
        ensureUniqueNom(existingMolecule.getNom(), id);
        return saveWithDuplicateHandling(existingMolecule);
    }

    public void delete(Long id) {
        findById(id);
        long usageCount = medicamentCompositionRepository.countByMolecule_Id(id);
        if (usageCount > 0) {
            throw new ReferenceDataInUseException(
                    "Suppression impossible : cette molecule est utilisee dans " + usageCount + " composition(s).");
        }
        moleculeRepository.deleteById(id);
    }

    private void ensureUniqueNom(String nom, Long currentId) {
        boolean exists = currentId == null
                ? moleculeRepository.existsByNomIgnoreCase(nom)
                : moleculeRepository.existsByNomIgnoreCaseAndIdNot(nom, currentId);

        if (exists) {
            throw new ReferenceDataDuplicateException(
                    "Une molécule avec le nom \"" + nom + "\" existe déjà."
            );
        }
    }

    private Molecule saveWithDuplicateHandling(Molecule molecule) {
        try {
            return moleculeRepository.save(molecule);
        } catch (DataIntegrityViolationException ex) {
            throw new ReferenceDataDuplicateException(
                    "Une molécule avec le nom \"" + molecule.getNom() + "\" existe déjà."
            );
        }
    }

    private String normalizeRequiredValue(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Le nom de la molécule est obligatoire.");
        }
        return value.trim();
    }
}

