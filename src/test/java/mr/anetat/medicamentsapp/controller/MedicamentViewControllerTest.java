package mr.anetat.medicamentsapp.controller;

import java.math.BigDecimal;
import java.util.List;

import mr.anetat.medicamentsapp.config.SecurityConfig;
import mr.anetat.medicamentsapp.dto.MedicamentSearchResultDto;
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
                .andExpect(content().string(containsString("Doliprane 1000 mg comprimé")));
    }
}


