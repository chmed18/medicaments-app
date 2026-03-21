package mr.anetat.medicamentsapp.controller;

import java.math.BigDecimal;
import java.util.List;

import mr.anetat.medicamentsapp.config.SecurityConfig;
import mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.service.MedicamentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(AdminMedicamentController.class)
@Import(SecurityConfig.class)
class AdminMedicamentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MedicamentAdminService medicamentAdminService;

    // -------------------------------------------------------------------------
    // LIST / SEARCH
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderAdminMedicamentListWithPagination() throws Exception {
        MedicamentAdminListItemDto item = new MedicamentAdminListItemDto(
                1L, "Doliprane 1000", "Doliprane 1000 mg comprime", "Comprime", "Sanofi",
                "Boite de 8", new BigDecimal("1500.00"), new BigDecimal("1200.00"),
                new BigDecimal("900.00"), 1L);

        when(medicamentAdminService.search(eq(null), any()))
                .thenReturn(new PageImpl<>(List.of(item), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/admin/medicaments"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/medicaments/list"))
                .andExpect(model().attributeExists("medicaments", "page", "query"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderFilteredListWhenQueryProvided() throws Exception {
        when(medicamentAdminService.search(eq("doliprane"), any()))
                .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 10), 0));

        mockMvc.perform(get("/admin/medicaments").param("q", "doliprane"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/medicaments/list"))
                .andExpect(model().attribute("query", "doliprane"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderCreateForm() throws Exception {
        when(medicamentAdminService.findAllFormes()).thenReturn(List.of());
        when(medicamentAdminService.findAllLaboratoires()).thenReturn(List.of());
        when(medicamentAdminService.findAllMolecules()).thenReturn(List.of());
        when(medicamentAdminService.findAllUnitesDosage()).thenReturn(List.of());

        mockMvc.perform(get("/admin/medicaments/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/medicaments/form"))
                .andExpect(model().attributeExists("form"))
                .andExpect(model().attribute("isEdit", false));
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteMedicamentAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/medicaments/5/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/medicaments"));

        verify(medicamentAdminService).delete(5L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRedirectWithErrorMessageWhenDeletingNonExistingMedicament() throws Exception {
        doThrow(new ResourceNotFoundException("Medicament introuvable."))
                .when(medicamentAdminService).delete(99L);

        mockMvc.perform(post("/admin/medicaments/99/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Medicament introuvable."));
    }

    // -------------------------------------------------------------------------
    // EDIT FORM
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRedirectWithErrorMessageWhenEditingNonExistingMedicament() throws Exception {
        doThrow(new ResourceNotFoundException("Medicament introuvable."))
                .when(medicamentAdminService).getFormById(999L);

        mockMvc.perform(get("/admin/medicaments/999/edit"))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attribute("errorMessage", "Medicament introuvable."));
    }

    // -------------------------------------------------------------------------
    // SECURITY
    // -------------------------------------------------------------------------

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidNonAdminUser() throws Exception {
        mockMvc.perform(get("/admin/medicaments"))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectAnonymousUser() throws Exception {
        mockMvc.perform(get("/admin/medicaments"))
                .andExpect(status().isUnauthorized());
    }
}
