package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.dto.MedicamentSearchResultDto;
import mr.anetat.medicamentsapp.service.MedicamentQueryService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MedicamentViewController {

    private final MedicamentQueryService medicamentQueryService;

    public MedicamentViewController(MedicamentQueryService medicamentQueryService) {
        this.medicamentQueryService = medicamentQueryService;
    }

    @GetMapping("/")
    public String showSearchPage(
            @RequestParam(name = "q", required = false) String q,
            @RequestParam(name = "query", required = false) String query,
            Model model) {
        String effectiveQuery = (query != null && !query.isBlank()) ? query : q;
        List<MedicamentSearchResultDto> results = medicamentQueryService.searchMedicaments(effectiveQuery);

        model.addAttribute("query", effectiveQuery);
        model.addAttribute("results", results);
        return "medicament-search";
    }
}

