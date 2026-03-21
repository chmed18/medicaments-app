package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.domain.GroupeEquivalence;
import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.domain.Medicament;
import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.dto.MedicamentAdminForm;
import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.FormeRepository;
import mr.anetat.medicamentsapp.repository.GroupeEquivalenceRepository;
import mr.anetat.medicamentsapp.repository.LaboratoireRepository;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import mr.anetat.medicamentsapp.repository.MoleculeRepository;
import mr.anetat.medicamentsapp.repository.UniteDosageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentAdminServiceTest {

    @Mock private MedicamentRepository medicamentRepository;
    @Mock private MedicamentCompositionRepository medicamentCompositionRepository;
    @Mock private GroupeEquivalenceRepository groupeEquivalenceRepository;
    @Mock private FormeRepository formeRepository;
    @Mock private LaboratoireRepository laboratoireRepository;
    @Mock private MoleculeRepository moleculeRepository;
    @Mock private UniteDosageRepository uniteDosageRepository;
    @Mock private EquivalenceSignatureService equivalenceSignatureService;

    @InjectMocks
    private MedicamentAdminService medicamentAdminService;

    // -------------------------------------------------------------------------
    // CREATE
    // -------------------------------------------------------------------------

    @Test
    void shouldReuseExistingEquivalenceGroupOnCreate() {
        MedicamentAdminForm form = buildValidForm();

        Forme forme = formeWithId(1L);
        Laboratoire laboratoire = laboratoireWithId(9L);
        Molecule molecule = moleculeWithId(5L);
        UniteDosage uniteDosage = uniteDosageWithId(3L);

        GroupeEquivalence existingGroup = new GroupeEquivalence();
        existingGroup.setId(100L);
        existingGroup.setSignature("5:500:3");

        when(formeRepository.findById(1L)).thenReturn(Optional.of(forme));
        when(laboratoireRepository.findById(9L)).thenReturn(Optional.of(laboratoire));
        when(moleculeRepository.findById(5L)).thenReturn(Optional.of(molecule));
        when(uniteDosageRepository.findById(3L)).thenReturn(Optional.of(uniteDosage));
        when(equivalenceSignatureService.generateSignature(form.getCompositions())).thenReturn("5:500:3");
        when(groupeEquivalenceRepository.findBySignature("5:500:3")).thenReturn(Optional.of(existingGroup));
        when(medicamentRepository.save(any(Medicament.class))).thenAnswer(inv -> {
            Medicament m = inv.getArgument(0);
            m.setId(77L);
            return m;
        });

        Medicament created = medicamentAdminService.create(form);

        assertThat(created.getId()).isEqualTo(77L);
        assertThat(created.getGroupeEquivalence()).isSameAs(existingGroup);
        verify(groupeEquivalenceRepository, never()).save(any(GroupeEquivalence.class));
        verify(medicamentCompositionRepository).saveAll(any());
    }

    @Test
    void shouldCreateNewEquivalenceGroupWhenSignatureNotFound() {
        MedicamentAdminForm form = buildValidForm();

        when(formeRepository.findById(1L)).thenReturn(Optional.of(formeWithId(1L)));
        when(laboratoireRepository.findById(9L)).thenReturn(Optional.of(laboratoireWithId(9L)));
        when(moleculeRepository.findById(5L)).thenReturn(Optional.of(moleculeWithId(5L)));
        when(uniteDosageRepository.findById(3L)).thenReturn(Optional.of(uniteDosageWithId(3L)));
        when(equivalenceSignatureService.generateSignature(form.getCompositions())).thenReturn("5:500:3");
        when(groupeEquivalenceRepository.findBySignature("5:500:3")).thenReturn(Optional.empty());
        when(groupeEquivalenceRepository.save(any(GroupeEquivalence.class))).thenAnswer(inv -> {
            GroupeEquivalence g = inv.getArgument(0);
            g.setId(200L);
            return g;
        });
        when(medicamentRepository.save(any(Medicament.class))).thenAnswer(inv -> {
            Medicament m = inv.getArgument(0);
            m.setId(88L);
            return m;
        });

        Medicament created = medicamentAdminService.create(form);

        assertThat(created.getGroupeEquivalence()).isNotNull();
        assertThat(created.getGroupeEquivalence().getId()).isEqualTo(200L);
        verify(groupeEquivalenceRepository).save(any(GroupeEquivalence.class));
    }

    @Test
    void shouldRejectCreateWhenLibelleIsBlank() {
        MedicamentAdminForm form = buildValidForm();
        form.setLibelle("  ");

        assertThatThrownBy(() -> medicamentAdminService.create(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("libelle");
    }

    @Test
    void shouldRejectCreateWhenNoComposition() {
        MedicamentAdminForm form = buildValidForm();
        form.setCompositions(List.of());

        assertThatThrownBy(() -> medicamentAdminService.create(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("composition");
    }

    @Test
    void shouldRejectCreateWhenDuplicateCompositionLines() {
        MedicamentAdminForm form = buildValidForm();
        // Deux lignes identiques (même molecule+dosage+unité)
        form.setCompositions(List.of(
                new MedicamentCompositionForm(5L, new BigDecimal("500.00"), 3L, 1),
                new MedicamentCompositionForm(5L, new BigDecimal("500.00"), 3L, 2)));

        assertThatThrownBy(() -> medicamentAdminService.create(form))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("uniques");
    }

    // -------------------------------------------------------------------------
    // UPDATE
    // -------------------------------------------------------------------------

    @Test
    void shouldUpdateMedicamentAndChangeEquivalenceGroupWhenCompositionChanges() {
        MedicamentAdminForm form = buildValidForm();
        // Nouvelle composition avec un dosage différent → nouvelle signature
        form.setCompositions(List.of(new MedicamentCompositionForm(5L, new BigDecimal("1000.00"), 3L, 1)));

        Medicament existing = new Medicament();
        existing.setId(42L);

        GroupeEquivalence newGroup = new GroupeEquivalence();
        newGroup.setId(300L);
        newGroup.setSignature("5:1000:3");

        when(medicamentRepository.findById(42L)).thenReturn(Optional.of(existing));
        when(formeRepository.findById(1L)).thenReturn(Optional.of(formeWithId(1L)));
        when(laboratoireRepository.findById(9L)).thenReturn(Optional.of(laboratoireWithId(9L)));
        when(moleculeRepository.findById(5L)).thenReturn(Optional.of(moleculeWithId(5L)));
        when(uniteDosageRepository.findById(3L)).thenReturn(Optional.of(uniteDosageWithId(3L)));
        when(equivalenceSignatureService.generateSignature(form.getCompositions())).thenReturn("5:1000:3");
        when(groupeEquivalenceRepository.findBySignature("5:1000:3")).thenReturn(Optional.of(newGroup));
        when(medicamentRepository.save(any(Medicament.class))).thenAnswer(inv -> inv.getArgument(0));

        Medicament updated = medicamentAdminService.update(42L, form);

        assertThat(updated.getGroupeEquivalence()).isSameAs(newGroup);
        verify(medicamentCompositionRepository).deleteByMedicament_Id(42L);
        verify(medicamentCompositionRepository).saveAll(any());
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenUpdatingNonExistingMedicament() {
        when(medicamentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicamentAdminService.update(999L, buildValidForm()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -------------------------------------------------------------------------
    // DELETE
    // -------------------------------------------------------------------------

    @Test
    void shouldDeleteMedicamentAndItsCompositions() {
        Medicament medicament = new Medicament();
        medicament.setId(55L);

        when(medicamentRepository.findById(55L)).thenReturn(Optional.of(medicament));

        medicamentAdminService.delete(55L);

        verify(medicamentCompositionRepository).deleteByMedicament_Id(55L);
        verify(medicamentRepository).delete(medicament);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenDeletingNonExistingMedicament() {
        when(medicamentRepository.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicamentAdminService.delete(404L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private MedicamentAdminForm buildValidForm() {
        MedicamentAdminForm form = new MedicamentAdminForm();
        form.setLibelle("Doliprane");
        form.setLibelleComplet("Doliprane 500 mg comprime");
        form.setFormeId(1L);
        form.setLaboratoireId(9L);
        form.setPresentation("Boite de 8");
        form.setPrixPharmacie(new BigDecimal("1500.00"));
        form.setPrixGrossiste(new BigDecimal("1200.00"));
        form.setPrixCamec(new BigDecimal("1000.00"));
        form.setSource("import");
        form.setCompositions(List.of(new MedicamentCompositionForm(5L, new BigDecimal("500.00"), 3L, 1)));
        return form;
    }

    private Forme formeWithId(Long id) {
        Forme f = new Forme(); f.setId(id); return f;
    }

    private Laboratoire laboratoireWithId(Long id) {
        Laboratoire l = new Laboratoire(); l.setId(id); return l;
    }

    private Molecule moleculeWithId(Long id) {
        Molecule m = new Molecule(); m.setId(id); return m;
    }

    private UniteDosage uniteDosageWithId(Long id) {
        UniteDosage u = new UniteDosage(); u.setId(id); return u;
    }
}
