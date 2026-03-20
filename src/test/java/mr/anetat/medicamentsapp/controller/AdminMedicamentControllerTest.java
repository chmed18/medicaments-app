package mr.anetat.medicamentsapp.controller;

import java.math.BigDecimal;
import java.util.List;

import mr.anetat.medicamentsapp.config.SecurityConfig;
import mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto;
import mr.anetat.medicamentsapp.service.MedicamentAdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderAdminMedicamentList() throws Exception {
        when(medicamentAdminService.findAllForAdmin()).thenReturn(List.of(
                new MedicamentAdminListItemDto(
                        1L,
                        "Doliprane 1000 mg comprime",
                        "Comprime",
                        "Sanofi",
                        "Boite de 8",
                        new BigDecimal("1500.00"),
                        1L)));

        mockMvc.perform(get("/admin/medicaments"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/medicaments/list"))
                .andExpect(model().attributeExists("medicaments"));
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

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteMedicamentAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/medicaments/5/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/medicaments"));

        verify(medicamentAdminService).delete(5L);
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidNonAdminUser() throws Exception {
        mockMvc.perform(get("/admin/medicaments"))
                .andExpect(status().isForbidden());
    }
}



