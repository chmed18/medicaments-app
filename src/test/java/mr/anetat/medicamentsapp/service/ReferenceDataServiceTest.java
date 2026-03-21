package mr.anetat.medicamentsapp.service;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.repository.FormeRepository;
import mr.anetat.medicamentsapp.repository.LaboratoireRepository;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import mr.anetat.medicamentsapp.repository.MoleculeRepository;
import mr.anetat.medicamentsapp.repository.UniteDosageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReferenceDataServiceTest {

    @Mock
    private FormeRepository formeRepository;

    @Mock
    private LaboratoireRepository laboratoireRepository;

    @Mock
    private MoleculeRepository moleculeRepository;

    @Mock
    private UniteDosageRepository uniteDosageRepository;

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private MedicamentCompositionRepository medicamentCompositionRepository;

    @InjectMocks
    private FormeService formeService;

    @InjectMocks
    private LaboratoireService laboratoireService;

    @InjectMocks
    private MoleculeService moleculeService;

    @InjectMocks
    private UniteDosageService uniteDosageService;

    @Test
    void shouldIgnoreProvidedIdWhenCreatingForme() {
        Forme forme = new Forme();
        forme.setId(99L);
        forme.setLibelle("Comprime");

        when(formeRepository.save(org.mockito.ArgumentMatchers.any(Forme.class))).thenAnswer(invocation -> invocation.getArgument(0));

        formeService.create(forme);

        ArgumentCaptor<Forme> captor = ArgumentCaptor.forClass(Forme.class);
        verify(formeRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(forme.getId()).isNull();
    }

    @Test
    void shouldIgnoreProvidedIdWhenCreatingLaboratoire() {
        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setId(88L);
        laboratoire.setNom("Sanofi");

        when(laboratoireRepository.save(org.mockito.ArgumentMatchers.any(Laboratoire.class))).thenAnswer(invocation -> invocation.getArgument(0));

        laboratoireService.create(laboratoire);

        ArgumentCaptor<Laboratoire> captor = ArgumentCaptor.forClass(Laboratoire.class);
        verify(laboratoireRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(laboratoire.getId()).isNull();
    }

    @Test
    void shouldIgnoreProvidedIdWhenCreatingMolecule() {
        Molecule molecule = new Molecule();
        molecule.setId(77L);
        molecule.setNom("Paracetamol");

        when(moleculeRepository.save(org.mockito.ArgumentMatchers.any(Molecule.class))).thenAnswer(invocation -> invocation.getArgument(0));

        moleculeService.create(molecule);

        ArgumentCaptor<Molecule> captor = ArgumentCaptor.forClass(Molecule.class);
        verify(moleculeRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(molecule.getId()).isNull();
    }

    @Test
    void shouldIgnoreProvidedIdWhenCreatingUniteDosage() {
        UniteDosage uniteDosage = new UniteDosage();
        uniteDosage.setId(66L);
        uniteDosage.setLibelle("mg");

        when(uniteDosageRepository.save(org.mockito.ArgumentMatchers.any(UniteDosage.class))).thenAnswer(invocation -> invocation.getArgument(0));

        uniteDosageService.create(uniteDosage);

        ArgumentCaptor<UniteDosage> captor = ArgumentCaptor.forClass(UniteDosage.class);
        verify(uniteDosageRepository).save(captor.capture());
        assertThat(captor.getValue().getId()).isNull();
        assertThat(uniteDosage.getId()).isNull();
    }

    @Test
    void shouldRejectDuplicateFormeIgnoringCase() {
        Forme forme = new Forme();
        forme.setLibelle("Comprime");

        when(formeRepository.existsByLibelleIgnoreCase("Comprime")).thenReturn(true);

        assertThatThrownBy(() -> formeService.create(forme))
                .isInstanceOf(ReferenceDataDuplicateException.class)
                .hasMessageContaining("forme")
                .hasMessageContaining("Comprime");
    }

    @Test
    void shouldRejectDuplicateLaboratoireIgnoringCase() {
        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setNom("Sanofi");

        when(laboratoireRepository.existsByNomIgnoreCase("Sanofi")).thenReturn(true);

        assertThatThrownBy(() -> laboratoireService.create(laboratoire))
                .isInstanceOf(ReferenceDataDuplicateException.class)
                .hasMessageContaining("laboratoire")
                .hasMessageContaining("Sanofi");
    }

    @Test
    void shouldRejectDuplicateMoleculeIgnoringCase() {
        Molecule molecule = new Molecule();
        molecule.setNom("Paracetamol");

        when(moleculeRepository.existsByNomIgnoreCase("Paracetamol")).thenReturn(true);

        assertThatThrownBy(() -> moleculeService.create(molecule))
                .isInstanceOf(ReferenceDataDuplicateException.class)
                .hasMessageContaining("molécule")
                .hasMessageContaining("Paracetamol");
    }

    @Test
    void shouldRejectDuplicateUniteDosageIgnoringCase() {
        UniteDosage uniteDosage = new UniteDosage();
        uniteDosage.setLibelle("mg");

        when(uniteDosageRepository.existsByLibelleIgnoreCase("mg")).thenReturn(true);

        assertThatThrownBy(() -> uniteDosageService.create(uniteDosage))
                .isInstanceOf(ReferenceDataDuplicateException.class)
                .hasMessageContaining("unité de dosage")
                .hasMessageContaining("mg");
    }
}

