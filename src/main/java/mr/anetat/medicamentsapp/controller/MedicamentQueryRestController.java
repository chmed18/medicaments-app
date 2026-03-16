package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.dto.MedicamentSearchResultDto;
import mr.anetat.medicamentsapp.service.MedicamentQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/medicaments")
public class MedicamentQueryRestController {

    private final MedicamentQueryService medicamentQueryService;

    public MedicamentQueryRestController(MedicamentQueryService medicamentQueryService) {
        this.medicamentQueryService = medicamentQueryService;
    }

    @GetMapping
    public List<MedicamentSearchResultDto> searchMedicaments(
            @RequestParam(name = "query", required = false) String query) {
        return medicamentQueryService.searchMedicaments(query);
    }

    @GetMapping("/ping")
    public String ping() {
        return "ok";
    }
}

