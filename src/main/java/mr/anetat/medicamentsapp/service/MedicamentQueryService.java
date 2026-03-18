package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Medicament;
import mr.anetat.medicamentsapp.domain.MedicamentComposition;
import mr.anetat.medicamentsapp.dto.EquivalentDto;
import mr.anetat.medicamentsapp.dto.MedicamentDetailDto;
import mr.anetat.medicamentsapp.dto.MedicamentSearchResultDto;
import mr.anetat.medicamentsapp.dto.MoleculeDto;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MedicamentQueryService {

    private final MedicamentRepository medicamentRepository;
    private final MedicamentCompositionRepository medicamentCompositionRepository;

    public MedicamentQueryService(
            MedicamentRepository medicamentRepository,
            MedicamentCompositionRepository medicamentCompositionRepository) {
        this.medicamentRepository = medicamentRepository;
        this.medicamentCompositionRepository = medicamentCompositionRepository;
    }

    public List<MedicamentSearchResultDto> searchMedicaments(String query) {
        if (query == null || query.isBlank()) {
            return List.of();
        }

        String normalizedQuery = query.trim();
        if (normalizedQuery.isEmpty()) {
            return List.of();
        }

        return medicamentRepository.searchByLabel(normalizedQuery)
                .stream()
                .map(this::mapToMedicamentSearchResult)
                .toList();
    }

    public List<String> getAutocompleteSuggestions(String query) {
        if (query == null || query.isBlank() || query.trim().length() < 2) {
            return List.of();
        }
        return medicamentRepository.findLibelleCompletSuggestions(query.trim(), PageRequest.of(0, 10));
    }

    public MedicamentDetailDto getMedicamentDetail(Long id) {
        if (id == null) {
            throw new ResourceNotFoundException("Médicament introuvable.");
        }

        Medicament medicament = medicamentRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Médicament introuvable pour l'identifiant " + id + "."));

        List<MoleculeDto> molecules = medicamentCompositionRepository.findDetailedByMedicamentId(id)
                .stream()
                .map(this::mapToMoleculeDto)
                .toList();

        List<EquivalentDto> equivalents = findEquivalentDtos(medicament);

        return new MedicamentDetailDto(
                medicament.getId(),
                medicament.getLibelle(),
                medicament.getLibelleComplet(),
                getFormeLabel(medicament),
                medicament.getPresentation(),
                medicament.getLaboratoire() != null ? medicament.getLaboratoire().getNom() : null,
                medicament.getPrixPharmacie(),
                molecules,
                equivalents);
    }

    private MedicamentSearchResultDto mapToMedicamentSearchResult(Medicament medicament) {
        return new MedicamentSearchResultDto(
                medicament.getId(),
                medicament.getLibelle(),
                medicament.getLibelleComplet(),
                medicament.getPresentation(),
                medicament.getPrixPharmacie(),
                medicament.getPrixGrossiste(),
                medicament.getPrixCamec());
    }

    private MoleculeDto mapToMoleculeDto(MedicamentComposition composition) {
        return new MoleculeDto(
                composition.getMolecule().getNom(),
                composition.getDosageValeur(),
                getUniteDosageLabel(composition));
    }

    private List<EquivalentDto> findEquivalentDtos(Medicament medicament) {
        if (medicament.getGroupeEquivalence() == null || medicament.getGroupeEquivalence().getId() == null) {
            return List.of();
        }

        return medicamentRepository.findEquivalentMedicaments(
                        medicament.getGroupeEquivalence().getId(),
                        medicament.getId())
                .stream()
                .map(this::mapToEquivalentDto)
                .toList();
    }

    private EquivalentDto mapToEquivalentDto(Medicament medicament) {
        return new EquivalentDto(
                medicament.getId(),
                medicament.getLibelleComplet(),
                medicament.getLaboratoire() != null ? medicament.getLaboratoire().getNom() : null,
                medicament.getPresentation(),
                medicament.getPrixPharmacie());
    }

    private String getFormeLabel(Medicament medicament) {
        if (medicament.getForme() == null) {
            return null;
        }
        if (medicament.getForme().getLibelleComplet() != null && !medicament.getForme().getLibelleComplet().isBlank()) {
            return medicament.getForme().getLibelleComplet();
        }
        return medicament.getForme().getLibelle();
    }

    private String getUniteDosageLabel(MedicamentComposition composition) {
        if (composition.getUniteDosage() == null) {
            return null;
        }
        if (composition.getUniteDosage().getLibelle() != null && !composition.getUniteDosage().getLibelle().isBlank()) {
            return composition.getUniteDosage().getLibelle();
        }
        return composition.getUniteDosage().getLibelleComplet();
    }
}

