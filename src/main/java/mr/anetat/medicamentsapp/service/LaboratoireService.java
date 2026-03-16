package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.LaboratoireRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LaboratoireService {

    private final LaboratoireRepository laboratoireRepository;

    public LaboratoireService(LaboratoireRepository laboratoireRepository) {
        this.laboratoireRepository = laboratoireRepository;
    }

    public List<Laboratoire> findAll() {
        return laboratoireRepository.findAll();
    }

    public Laboratoire findById(Long id) {
        return laboratoireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratoire not found with id: " + id));
    }

    public Laboratoire create(Laboratoire laboratoire) {
        return laboratoireRepository.save(laboratoire);
    }

    public Laboratoire update(Long id, Laboratoire laboratoire) {
        Laboratoire existingLaboratoire = findById(id);
        existingLaboratoire.setNom(laboratoire.getNom());
        existingLaboratoire.setAdresse(laboratoire.getAdresse());
        return laboratoireRepository.save(existingLaboratoire);
    }

    public void delete(Long id) {
        findById(id);
        laboratoireRepository.deleteById(id);
    }
}

