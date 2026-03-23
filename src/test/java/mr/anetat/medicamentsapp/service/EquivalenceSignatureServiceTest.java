package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.util.List;

import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EquivalenceSignatureServiceTest {

    private final EquivalenceSignatureService equivalenceSignatureService = new EquivalenceSignatureService();

    @Test
    void shouldGenerateCanonicalSignatureSortedByMoleculeId() {
        // Deux molécules saisies dans un ordre quelconque : le tri canonique garantit molecule 1 avant molecule 2
        List<MedicamentCompositionForm> compositions = List.of(
                new MedicamentCompositionForm(2L, new BigDecimal("500.00"), 3L, 2),
                new MedicamentCompositionForm(1L, new BigDecimal("1000.0"), 4L, 1));

        String signature = equivalenceSignatureService.generateSignature(compositions);

        assertThat(signature).isEqualTo("1:1000.00:4|2:500.00:3");
    }

    @Test
    void shouldNormalizeDecimalValuesInSignature() {
        List<MedicamentCompositionForm> compositions = List.of(
                new MedicamentCompositionForm(10L, new BigDecimal("1.50"), 8L, null));

        String signature = equivalenceSignatureService.generateSignature(compositions);

        assertThat(signature).isEqualTo("10:1.50:8");
    }

    @Test
    void shouldProduceSameSignatureRegardlessOfOrdreAffichage() {
        // Même composition avec ordreAffichage différents → même signature
        List<MedicamentCompositionForm> compositions1 = List.of(
                new MedicamentCompositionForm(3L, new BigDecimal("250"), 5L, 1),
                new MedicamentCompositionForm(7L, new BigDecimal("125"), 2L, 2));

        List<MedicamentCompositionForm> compositions2 = List.of(
                new MedicamentCompositionForm(7L, new BigDecimal("125"), 2L, 1),
                new MedicamentCompositionForm(3L, new BigDecimal("250"), 5L, 2));

        String sig1 = equivalenceSignatureService.generateSignature(compositions1);
        String sig2 = equivalenceSignatureService.generateSignature(compositions2);

        assertThat(sig1).isEqualTo(sig2).isEqualTo("3:250.00:5|7:125.00:2");
    }

    @Test
    void shouldFormatIntegerDosageWithTwoDecimals() {
        List<MedicamentCompositionForm> compositions = List.of(
                new MedicamentCompositionForm(42L, new BigDecimal("500"), 9L, 1));

        String signature = equivalenceSignatureService.generateSignature(compositions);

        assertThat(signature).isEqualTo("42:500.00:9");
    }

    @Test
    void shouldReturnEmptySignatureForEmptyCompositions() {
        String signature = equivalenceSignatureService.generateSignature(List.of());

        assertThat(signature).isEmpty();
    }
}

