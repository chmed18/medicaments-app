package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import org.springframework.stereotype.Service;

@Service
public class EquivalenceSignatureService {

    public String generateSignature(Long formeId, List<MedicamentCompositionForm> compositions) {
        String compositionSignature = compositions.stream()
                .filter(Objects::nonNull)
                .sorted(Comparator
                        .comparing(MedicamentCompositionForm::getOrdreAffichage,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(MedicamentCompositionForm::getMoleculeId)
                        .thenComparing(line -> normalizeDecimal(line.getDosageValeur()))
                        .thenComparing(MedicamentCompositionForm::getUniteDosageId))
                .map(this::toCompositionToken)
                .reduce((left, right) -> left + "|" + right)
                .orElse("");

        return (formeId == null ? "forme:null" : "forme:" + formeId) + "|" + compositionSignature;
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
            return "0";
        }
        return value.stripTrailingZeros().toPlainString();
    }
}


