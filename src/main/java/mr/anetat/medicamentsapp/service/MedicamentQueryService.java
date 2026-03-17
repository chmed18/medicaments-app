package mr.anetat.medicamentsapp.service;

import java.util.List;

import mr.anetat.medicamentsapp.domain.Medicament;
import mr.anetat.medicamentsapp.dto.MedicamentSearchResultDto;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Service
public class MedicamentQueryService {

    private final MedicamentRepository medicamentRepository;

    public MedicamentQueryService(MedicamentRepository medicamentRepository) {
        this.medicamentRepository = medicamentRepository;
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
}

