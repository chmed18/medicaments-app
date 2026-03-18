package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.domain.GroupeEquivalence;
import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.domain.Medicament;
import mr.anetat.medicamentsapp.domain.MedicamentComposition;
import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.dto.MedicamentDetailDto;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MedicamentQueryServiceTest {

    @Mock
    private MedicamentRepository medicamentRepository;

    @Mock
    private MedicamentCompositionRepository medicamentCompositionRepository;

    @InjectMocks
    private MedicamentQueryService medicamentQueryService;

    @Test
    void shouldReturnDetailedMedicationWithCompositionAndEquivalents() {
        Medicament medicament = buildMedicament(1L, "Doliprane", "Doliprane 1000 mg comprimé", "Sanofi", "Boîte de 8", "1500.00");
        GroupeEquivalence groupeEquivalence = new GroupeEquivalence();
        groupeEquivalence.setId(10L);
        medicament.setGroupeEquivalence(groupeEquivalence);

        MedicamentComposition composition = new MedicamentComposition();
        composition.setMedicament(medicament);
        composition.setDosageValeur(new BigDecimal("1000.00"));
        composition.setOrdreAffichage(1);

        Molecule molecule = new Molecule();
        molecule.setNom("Paracétamol");
        composition.setMolecule(molecule);

        UniteDosage uniteDosage = new UniteDosage();
        uniteDosage.setLibelle("mg");
        composition.setUniteDosage(uniteDosage);

        Medicament equivalent = buildMedicament(2L, "Paracetamol", "Paracetamol 1000 mg comprimé", "Pharma Plus", "Boîte de 10", "1300.00");

        when(medicamentRepository.findDetailById(1L)).thenReturn(Optional.of(medicament));
        when(medicamentCompositionRepository.findDetailedByMedicamentId(1L)).thenReturn(List.of(composition));
        when(medicamentRepository.findEquivalentMedicaments(10L, 1L)).thenReturn(List.of(equivalent));

        MedicamentDetailDto result = medicamentQueryService.getMedicamentDetail(1L);

        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getLibelle()).isEqualTo("Doliprane");
        assertThat(result.getLibelleComplet()).isEqualTo("Doliprane 1000 mg comprimé");
        assertThat(result.getForme()).isEqualTo("Comprimé");
        assertThat(result.getPresentation()).isEqualTo("Boîte de 8");
        assertThat(result.getLaboratoire()).isEqualTo("Sanofi");
        assertThat(result.getPrixPharmacie()).isEqualByComparingTo("1500.00");
        assertThat(result.getMolecules()).hasSize(1);
        assertThat(result.getMolecules().getFirst().getNom()).isEqualTo("Paracétamol");
        assertThat(result.getMolecules().getFirst().getDosageValeur()).isEqualByComparingTo("1000.00");
        assertThat(result.getMolecules().getFirst().getUniteDosage()).isEqualTo("mg");
        assertThat(result.getEquivalents()).hasSize(1);
        assertThat(result.getEquivalents().getFirst().getId()).isEqualTo(2L);
        assertThat(result.getEquivalents().getFirst().getLibelleComplet()).isEqualTo("Paracetamol 1000 mg comprimé");
        assertThat(result.getEquivalents().getFirst().getLaboratoire()).isEqualTo("Pharma Plus");
    }

    @Test
    void shouldReturnEmptyEquivalentsWhenMedicationHasNoEquivalenceGroup() {
        Medicament medicament = buildMedicament(4L, "Ibuprofène", "Ibuprofène 400 mg comprimé", "MediLab", "Boîte de 12", "2000.00");

        when(medicamentRepository.findDetailById(4L)).thenReturn(Optional.of(medicament));
        when(medicamentCompositionRepository.findDetailedByMedicamentId(4L)).thenReturn(List.of());

        MedicamentDetailDto result = medicamentQueryService.getMedicamentDetail(4L);

        assertThat(result.getEquivalents()).isEmpty();
        verify(medicamentRepository).findDetailById(4L);
        verify(medicamentCompositionRepository).findDetailedByMedicamentId(4L);
    }

    @Test
    void shouldThrowWhenMedicationIsNotFound() {
        when(medicamentRepository.findDetailById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> medicamentQueryService.getMedicamentDetail(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    private Medicament buildMedicament(
            Long id,
            String libelle,
            String libelleComplet,
            String laboratoireNom,
            String presentation,
            String prixPharmacie) {
        Medicament medicament = new Medicament();
        medicament.setId(id);
        medicament.setLibelle(libelle);
        medicament.setLibelleComplet(libelleComplet);
        medicament.setPresentation(presentation);
        medicament.setPrixPharmacie(new BigDecimal(prixPharmacie));

        Forme forme = new Forme();
        forme.setLibelle("Comprimé");
        forme.setLibelleComplet("Comprimé");
        medicament.setForme(forme);

        Laboratoire laboratoire = new Laboratoire();
        laboratoire.setNom(laboratoireNom);
        medicament.setLaboratoire(laboratoire);
        return medicament;
    }
}

