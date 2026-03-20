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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentAdminServiceTest {

    @Mock
    private MedicamentRepository medicamentRepository;
    @Mock
    private MedicamentCompositionRepository medicamentCompositionRepository;
    @Mock
    private GroupeEquivalenceRepository groupeEquivalenceRepository;
    @Mock
    private FormeRepository formeRepository;
    @Mock
    private LaboratoireRepository laboratoireRepository;
    @Mock
    private MoleculeRepository moleculeRepository;
    @Mock
    private UniteDosageRepository uniteDosageRepository;
    @Mock
    private EquivalenceSignatureService equivalenceSignatureService;

    @InjectMocks
    private MedicamentAdminService medicamentAdminService;

    @Test
    void shouldReuseExistingEquivalenceGroupOnCreate() {
        MedicamentAdminForm form = buildValidForm();

        Forme forme = new Forme();
        forme.setId(1L);

        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setId(9L);

        Molecule molecule = new Molecule();
        molecule.setId(5L);

        UniteDosage uniteDosage = new UniteDosage();
        uniteDosage.setId(3L);

        GroupeEquivalence existingGroup = new GroupeEquivalence();
        existingGroup.setId(100L);
        existingGroup.setSignature("forme:1|5:500:3");

        when(formeRepository.findById(1L)).thenReturn(Optional.of(forme));
        when(laboratoireRepository.findById(9L)).thenReturn(Optional.of(laboratoire));
        when(moleculeRepository.findById(5L)).thenReturn(Optional.of(molecule));
        when(uniteDosageRepository.findById(3L)).thenReturn(Optional.of(uniteDosage));
        when(equivalenceSignatureService.generateSignature(1L, form.getCompositions())).thenReturn("forme:1|5:500:3");
        when(groupeEquivalenceRepository.findBySignature("forme:1|5:500:3")).thenReturn(Optional.of(existingGroup));
        when(medicamentRepository.save(any(Medicament.class))).thenAnswer(invocation -> {
            Medicament medicament = invocation.getArgument(0);
            medicament.setId(77L);
            return medicament;
        });

        Medicament created = medicamentAdminService.create(form);

        assertThat(created.getId()).isEqualTo(77L);
        assertThat(created.getGroupeEquivalence()).isSameAs(existingGroup);
        verify(groupeEquivalenceRepository, times(0)).save(any(GroupeEquivalence.class));
        verify(medicamentCompositionRepository).saveAll(any());
    }

    @Test
    void shouldCreateNewEquivalenceGroupWhenSignatureNotFound() {
        MedicamentAdminForm form = buildValidForm();

        Forme forme = new Forme();
        forme.setId(1L);

        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setId(9L);

        Molecule molecule = new Molecule();
        molecule.setId(5L);

        UniteDosage uniteDosage = new UniteDosage();
        uniteDosage.setId(3L);

        when(formeRepository.findById(1L)).thenReturn(Optional.of(forme));
        when(laboratoireRepository.findById(9L)).thenReturn(Optional.of(laboratoire));
        when(moleculeRepository.findById(5L)).thenReturn(Optional.of(molecule));
        when(uniteDosageRepository.findById(3L)).thenReturn(Optional.of(uniteDosage));
        when(equivalenceSignatureService.generateSignature(1L, form.getCompositions())).thenReturn("forme:1|5:500:3");
        when(groupeEquivalenceRepository.findBySignature("forme:1|5:500:3")).thenReturn(Optional.empty());
        when(groupeEquivalenceRepository.save(any(GroupeEquivalence.class))).thenAnswer(invocation -> {
            GroupeEquivalence group = invocation.getArgument(0);
            group.setId(200L);
            return group;
        });
        when(medicamentRepository.save(any(Medicament.class))).thenAnswer(invocation -> {
            Medicament medicament = invocation.getArgument(0);
            medicament.setId(88L);
            return medicament;
        });

        Medicament created = medicamentAdminService.create(form);

        assertThat(created.getGroupeEquivalence()).isNotNull();
        assertThat(created.getGroupeEquivalence().getId()).isEqualTo(200L);
        verify(groupeEquivalenceRepository).save(any(GroupeEquivalence.class));
    }

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
}


