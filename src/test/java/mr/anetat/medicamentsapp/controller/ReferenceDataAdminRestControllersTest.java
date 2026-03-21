package mr.anetat.medicamentsapp.controller;

import java.util.List;

import mr.anetat.medicamentsapp.config.SecurityConfig;
import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.service.FormeService;
import mr.anetat.medicamentsapp.service.LaboratoireService;
import mr.anetat.medicamentsapp.service.MoleculeService;
import mr.anetat.medicamentsapp.service.UniteDosageService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest({
        FormeAdminRestController.class,
        LaboratoireAdminRestController.class,
        MoleculeAdminRestController.class,
        UniteDosageAdminRestController.class
})
@Import(SecurityConfig.class)
class ReferenceDataAdminRestControllersTest {

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
    void shouldListFormes() throws Exception {
        Forme forme = new Forme();
        forme.setId(1L);
        forme.setLibelle("Comprime");

        when(formeService.findAll()).thenReturn(List.of(forme));

        mockMvc.perform(get("/admin/formes").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].libelle").value("Comprime"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateLaboratoire() throws Exception {
        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setId(10L);
        laboratoire.setNom("Sanofi");
        laboratoire.setAdresse("Casablanca");

        when(laboratoireService.create(org.mockito.ArgumentMatchers.any(Laboratoire.class))).thenReturn(laboratoire);

        mockMvc.perform(post("/admin/laboratoires")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Sanofi\",\"adresse\":\"Casablanca\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.nom").value("Sanofi"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateMolecule() throws Exception {
        Molecule molecule = new Molecule();
        molecule.setId(4L);
        molecule.setNom("Paracetamol");

        when(moleculeService.update(org.mockito.ArgumentMatchers.eq(4L), org.mockito.ArgumentMatchers.any(Molecule.class)))
                .thenReturn(molecule);

        mockMvc.perform(put("/admin/molecules/4")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Paracetamol\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(4))
                .andExpect(jsonPath("$.nom").value("Paracetamol"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteUniteDosage() throws Exception {
        mockMvc.perform(delete("/admin/unites-dosage/5").with(csrf()))
                .andExpect(status().isNoContent());

        verify(uniteDosageService).delete(5L);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnExplicitConflictMessageWhenDuplicateLaboratoireExistsIgnoringCase() throws Exception {
        doThrow(new ReferenceDataDuplicateException(
                "Un laboratoire avec le nom \"Sanofi\" existe déjà."))
                .when(laboratoireService).create(org.mockito.ArgumentMatchers.any(Laboratoire.class));

        mockMvc.perform(post("/admin/laboratoires")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nom\":\"Sanofi\",\"adresse\":\"Casablanca\"}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Un laboratoire avec le nom \"Sanofi\" existe déjà."));
    }

    @Test
    @WithMockUser(roles = "USER")
    void shouldForbidNonAdminOnAdminApi() throws Exception {
        mockMvc.perform(get("/admin/formes"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnNotFoundWhenFormeDoesNotExist() throws Exception {
        doThrow(new ResourceNotFoundException("Forme not found with id: 999"))
                .when(formeService).findById(999L);

        mockMvc.perform(get("/admin/formes/999"))
                .andExpect(status().isNotFound());
    }
}

