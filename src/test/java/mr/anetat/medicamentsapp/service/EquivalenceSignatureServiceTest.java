package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.util.List;

import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class EquivalenceSignatureServiceTest {

    private final EquivalenceSignatureService equivalenceSignatureService = new EquivalenceSignatureService();

    @Test
    void shouldGenerateCanonicalSignatureSortedAndNormalized() {
        List<MedicamentCompositionForm> compositions = List.of(
                new MedicamentCompositionForm(2L, new BigDecimal("500.00"), 3L, 2),
                new MedicamentCompositionForm(1L, new BigDecimal("1000.0"), 4L, 1));

        String signature = equivalenceSignatureService.generateSignature(7L, compositions);

        assertThat(signature).isEqualTo("forme:7|1:1000:4|2:500:3");
    }

    @Test
    void shouldIncludeNullFormeInSignature() {
        List<MedicamentCompositionForm> compositions = List.of(
                new MedicamentCompositionForm(10L, new BigDecimal("1.50"), 8L, null));

        String signature = equivalenceSignatureService.generateSignature(null, compositions);

        assertThat(signature).isEqualTo("forme:null|10:1.5:8");
    }
}

