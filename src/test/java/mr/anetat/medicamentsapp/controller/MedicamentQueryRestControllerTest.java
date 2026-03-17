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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MedicamentQueryRestController.class)
@Import(SecurityConfig.class)
class MedicamentQueryRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicamentQueryService medicamentQueryService;

    @Test
    @WithMockUser
    void shouldReturnAutocompleteSuggestions() throws Exception {
        when(medicamentQueryService.getAutocompleteSuggestions("Do"))
                .thenReturn(List.of("Doliprane 1000 mg comprimé", "Dopamine injectable"));

        mockMvc.perform(get("/api/medicaments/autocomplete")
                        .param("query", "Do")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0]").value("Doliprane 1000 mg comprimé"))
                .andExpect(jsonPath("$[1]").value("Dopamine injectable"));
    }

    @Test
    @WithMockUser
    void shouldReturnSearchResultsAsJson() throws Exception {
        when(medicamentQueryService.searchMedicaments("Doli"))
                .thenReturn(List.of(new MedicamentSearchResultDto(
                        1L,
                        "Doliprane",
                        "Doliprane 1000 mg comprimé",
                        "Boîte de 8",
                        new BigDecimal("1500"),
                        new BigDecimal("1200"),
                        new BigDecimal("1000"))));

        mockMvc.perform(get("/api/medicaments")
                        .param("query", "Doli")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].libelle").value("Doliprane"))
                .andExpect(jsonPath("$[0].libelleComplet").value("Doliprane 1000 mg comprimé"))
                .andExpect(jsonPath("$[0].prixPharmacie").value(1500));
    }
}


