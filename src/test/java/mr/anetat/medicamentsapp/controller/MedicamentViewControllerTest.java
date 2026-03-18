package mr.anetat.medicamentsapp.controller;

import java.math.BigDecimal;
import java.util.List;

import mr.anetat.medicamentsapp.config.SecurityConfig;
import mr.anetat.medicamentsapp.dto.EquivalentDto;
import mr.anetat.medicamentsapp.dto.MedicamentDetailDto;
import mr.anetat.medicamentsapp.dto.MedicamentSearchResultDto;
import mr.anetat.medicamentsapp.dto.MoleculeDto;
import mr.anetat.medicamentsapp.service.MedicamentQueryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(MedicamentViewController.class)
@Import(SecurityConfig.class)
class MedicamentViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicamentQueryService medicamentQueryService;

    @Test
    @WithMockUser
    void shouldRenderSearchPageWithExternalizedAssetsAndAutocompleteConfiguration() throws Exception {
        List<MedicamentSearchResultDto> results = List.of(
                new MedicamentSearchResultDto(
                        1L,
                        "Doliprane",
                        "Doliprane 1000 mg comprimé",
                        "Boîte de 8",
                        new BigDecimal("1500"),
                        null,
                        null));

        when(medicamentQueryService.searchMedicaments("Doliprane")).thenReturn(results);

        mockMvc.perform(get("/").param("query", "Doliprane"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicament-search"))
                .andExpect(model().attribute("query", "Doliprane"))
                .andExpect(model().attribute("results", results))
                .andExpect(content().string(containsString("/css/medicament-search.css")))
                .andExpect(content().string(containsString("/js/medicament-search.js")))
                .andExpect(content().string(containsString("data-autocomplete-url=\"/api/medicaments/autocomplete\"")))
                .andExpect(content().string(containsString("Doliprane 1000 mg comprimé")))
                .andExpect(content().string(containsString("/medicaments/1")));
    }

    @Test
    @WithMockUser
    void shouldRenderMedicationDetailPage() throws Exception {
        MedicamentDetailDto detail = new MedicamentDetailDto(
                1L,
                "Doliprane",
                "Doliprane 1000 mg comprimé",
                "Comprimé",
                "Boîte de 8",
                "Sanofi",
                new BigDecimal("1500"),
                List.of(new MoleculeDto("Paracétamol", new BigDecimal("1000"), "mg")),
                List.of(new EquivalentDto(2L, "Paracetamol 1000 mg comprimé", "Pharma Plus", "Boîte de 10", new BigDecimal("1300"))));

        when(medicamentQueryService.getMedicamentDetail(1L)).thenReturn(detail);

        mockMvc.perform(get("/medicaments/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("medicament-detail"))
                .andExpect(model().attribute("medicament", detail))
                .andExpect(content().string(containsString("Fiche médicament")))
                .andExpect(content().string(containsString("Paracétamol")))
                .andExpect(content().string(containsString("Paracetamol 1000 mg comprimé")))
                .andExpect(content().string(containsString("Retour à la recherche")));
    }
}


