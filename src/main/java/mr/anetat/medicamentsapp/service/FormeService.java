package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.FormeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class FormeService {

    private final FormeRepository formeRepository;

    public FormeService(FormeRepository formeRepository) {
        this.formeRepository = formeRepository;
    }

    public List<Forme> findAll() {
        return formeRepository.findAll();
    }

    public Forme findById(Long id) {
        return formeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Forme not found with id: " + id));
    }

    public Forme create(Forme forme) {
        return formeRepository.save(forme);
    }

    public Forme update(Long id, Forme forme) {
        Forme existingForme = findById(id);
        existingForme.setLibelle(forme.getLibelle());
        existingForme.setLibelleComplet(forme.getLibelleComplet());
        return formeRepository.save(existingForme);
    }

    public void delete(Long id) {
        findById(id);
        formeRepository.deleteById(id);
    }
}

