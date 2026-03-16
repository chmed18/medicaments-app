package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.MoleculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MoleculeService {

    private final MoleculeRepository moleculeRepository;

    public MoleculeService(MoleculeRepository moleculeRepository) {
        this.moleculeRepository = moleculeRepository;
    }

    public List<Molecule> findAll() {
        return moleculeRepository.findAll();
    }

    public Molecule findById(Long id) {
        return moleculeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Molecule not found with id: " + id));
    }

    public Molecule create(Molecule molecule) {
        return moleculeRepository.save(molecule);
    }

    public Molecule update(Long id, Molecule molecule) {
        Molecule existingMolecule = findById(id);
        existingMolecule.setNom(molecule.getNom());
        return moleculeRepository.save(existingMolecule);
    }

    public void delete(Long id) {
        findById(id);
        moleculeRepository.deleteById(id);
    }
}

