package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.config.SecurityConfig;
import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.service.FormeService;
import mr.anetat.medicamentsapp.service.LaboratoireService;
import mr.anetat.medicamentsapp.service.MoleculeService;
import mr.anetat.medicamentsapp.service.UniteDosageService;
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

@WebMvcTest(ReferenceDataAdminViewController.class)
@Import(SecurityConfig.class)
class ReferenceDataAdminViewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FormeService formeService;

    @MockitoBean
    private LaboratoireService laboratoireService;

    @MockitoBean
    private MoleculeService moleculeService;

    @MockitoBean
    private UniteDosageService uniteDosageService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderFormesListPage() throws Exception {
        Forme forme = new Forme();
        forme.setId(1L);
        forme.setLibelle("Comprime");

        when(formeService.search(eq("com"), any()))
                .thenReturn(new PageImpl<>(List.of(forme), PageRequest.of(0, 10), 1));

        mockMvc.perform(get("/admin/referentiels/formes").param("q", "com"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/referentiels/list"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("type"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldRenderCreatePageForMolecules() throws Exception {
        mockMvc.perform(get("/admin/referentiels/molecules/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/referentiels/form"))
                .andExpect(model().attribute("isEdit", false));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteFormeAndRedirect() throws Exception {
        mockMvc.perform(post("/admin/referentiels/formes/4/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/admin/referentiels/formes"));

        verify(formeService).delete(4L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowErrorMessageWhenDeleteBlocked() throws Exception {
        doThrow(new ReferenceDataInUseException("Suppression impossible"))
                .when(formeService).delete(7L);

        mockMvc.perform(post("/admin/referentiels/formes/7/delete").with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldShowExplicitDuplicateMessageOnCreateForm() throws Exception {
        doThrow(new ReferenceDataDuplicateException(
                "Une forme avec le libellé \"Comprime\" existe déjà."))
                .when(formeService).create(any(Forme.class));

        mockMvc.perform(post("/admin/referentiels/formes")
                        .with(csrf())
                        .param("primaryValue", "Comprime")
                        .param("secondaryValue", "Comprimé"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/referentiels/form"))
                .andExpect(model().attributeHasFieldErrors("form", "primaryValue"))
                .andExpect(model().attributeHasFieldErrorCode("form", "primaryValue", "duplicate"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidNonAdminUser() throws Exception {
        mockMvc.perform(get("/admin/referentiels/formes"))
                .andExpect(status().isForbidden());
    }
}

