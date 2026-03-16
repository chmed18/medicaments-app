package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.UniteDosageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class UniteDosageService {

    private final UniteDosageRepository uniteDosageRepository;

    public UniteDosageService(UniteDosageRepository uniteDosageRepository) {
        this.uniteDosageRepository = uniteDosageRepository;
    }

    public List<UniteDosage> findAll() {
        return uniteDosageRepository.findAll();
    }

    public UniteDosage findById(Long id) {
        return uniteDosageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("UniteDosage not found with id: " + id));
    }

    public UniteDosage create(UniteDosage uniteDosage) {
        return uniteDosageRepository.save(uniteDosage);
    }

    public UniteDosage update(Long id, UniteDosage uniteDosage) {
        UniteDosage existingUniteDosage = findById(id);
        existingUniteDosage.setLibelle(uniteDosage.getLibelle());
        existingUniteDosage.setLibelleComplet(uniteDosage.getLibelleComplet());
        return uniteDosageRepository.save(existingUniteDosage);
    }

    public void delete(Long id) {
        findById(id);
        uniteDosageRepository.deleteById(id);
    }
}

