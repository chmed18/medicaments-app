package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import org.springframework.stereotype.Service;

@Service
public class EquivalenceSignatureService {

    /**
     * Génère la signature d'équivalence à partir des lignes de composition.
     * Format : molecule_id:dosage:unite_dosage_id, séparateur | (si plusieurs molécules).
     * Le tri est canonique (moleculeId, dosage, uniteDosageId) pour garantir
     * la même signature quel que soit l'ordre de saisie.
     */
    public String generateSignature(List<MedicamentCompositionForm> compositions) {
        return compositions.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(MedicamentCompositionForm::getMoleculeId)
                        .thenComparing(line -> normalizeDecimal(line.getDosageValeur()))
                        .thenComparing(MedicamentCompositionForm::getUniteDosageId))
                .map(this::toCompositionToken)
                .collect(Collectors.joining("|"));
    }

    private String toCompositionToken(MedicamentCompositionForm line) {
        return line.getMoleculeId()
                + ":"
                + normalizeDecimal(line.getDosageValeur())
                + ":"
                + line.getUniteDosageId();
    }

    private String normalizeDecimal(BigDecimal value) {
        if (value == null) {
            return "0.00";
        }
        return value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }
}
