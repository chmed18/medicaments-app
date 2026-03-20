package mr.anetat.medicamentsapp.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.domain.GroupeEquivalence;
import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.domain.Medicament;
import mr.anetat.medicamentsapp.domain.MedicamentComposition;
import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.dto.MedicamentAdminForm;
import mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto;
import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.repository.FormeRepository;
import mr.anetat.medicamentsapp.repository.GroupeEquivalenceRepository;
import mr.anetat.medicamentsapp.repository.LaboratoireRepository;
import mr.anetat.medicamentsapp.repository.MedicamentCompositionRepository;
import mr.anetat.medicamentsapp.repository.MedicamentRepository;
import mr.anetat.medicamentsapp.repository.MoleculeRepository;
import mr.anetat.medicamentsapp.repository.UniteDosageRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class MedicamentAdminService {

    private final MedicamentRepository medicamentRepository;
    private final MedicamentCompositionRepository medicamentCompositionRepository;
    private final GroupeEquivalenceRepository groupeEquivalenceRepository;
    private final FormeRepository formeRepository;
    private final LaboratoireRepository laboratoireRepository;
    private final MoleculeRepository moleculeRepository;
    private final UniteDosageRepository uniteDosageRepository;
    private final EquivalenceSignatureService equivalenceSignatureService;

    public MedicamentAdminService(
            MedicamentRepository medicamentRepository,
            MedicamentCompositionRepository medicamentCompositionRepository,
            GroupeEquivalenceRepository groupeEquivalenceRepository,
            FormeRepository formeRepository,
            LaboratoireRepository laboratoireRepository,
            MoleculeRepository moleculeRepository,
            UniteDosageRepository uniteDosageRepository,
            EquivalenceSignatureService equivalenceSignatureService) {
        this.medicamentRepository = medicamentRepository;
        this.medicamentCompositionRepository = medicamentCompositionRepository;
        this.groupeEquivalenceRepository = groupeEquivalenceRepository;
        this.formeRepository = formeRepository;
        this.laboratoireRepository = laboratoireRepository;
        this.moleculeRepository = moleculeRepository;
        this.uniteDosageRepository = uniteDosageRepository;
        this.equivalenceSignatureService = equivalenceSignatureService;
    }

    @Transactional(readOnly = true)
    public List<MedicamentAdminListItemDto> findAllForAdmin() {
        return medicamentRepository.findAllForAdminList();
    }

    @Transactional(readOnly = true)
    public MedicamentAdminForm getFormById(Long id) {
        Medicament medicament = medicamentRepository.findDetailById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicament not found with id: " + id));

        List<MedicamentCompositionForm> compositionForms = medicamentCompositionRepository.findDetailedByMedicamentId(id)
                .stream()
                .map(this::toCompositionForm)
                .toList();

        MedicamentAdminForm form = new MedicamentAdminForm();
        form.setLibelle(medicament.getLibelle());
        form.setLibelleComplet(medicament.getLibelleComplet());
        form.setFormeId(medicament.getForme() != null ? medicament.getForme().getId() : null);
        form.setLaboratoireId(medicament.getLaboratoire() != null ? medicament.getLaboratoire().getId() : null);
        form.setPresentation(medicament.getPresentation());
        form.setPrixPharmacie(medicament.getPrixPharmacie());
        form.setPrixGrossiste(medicament.getPrixGrossiste());
        form.setPrixCamec(medicament.getPrixCamec());
        form.setSource(medicament.getSource());
        form.setCompositions(new ArrayList<>(compositionForms));

        return form;
    }

    public Medicament create(MedicamentAdminForm form) {
        validateForm(form);

        Medicament medicament = new Medicament();
        applyBaseFields(medicament, form);
        medicament.setGroupeEquivalence(resolveGroupeEquivalence(form));

        Medicament saved = medicamentRepository.save(medicament);
        replaceCompositions(saved, form.getCompositions());
        return saved;
    }

    public Medicament update(Long id, MedicamentAdminForm form) {
        validateForm(form);

        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicament not found with id: " + id));

        applyBaseFields(medicament, form);
        medicament.setGroupeEquivalence(resolveGroupeEquivalence(form));

        Medicament saved = medicamentRepository.save(medicament);
        replaceCompositions(saved, form.getCompositions());
        return saved;
    }

    public void delete(Long id) {
        Medicament medicament = medicamentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Medicament not found with id: " + id));

        medicamentCompositionRepository.deleteByMedicament_Id(id);
        medicamentRepository.delete(medicament);
    }

    @Transactional(readOnly = true)
    public List<Forme> findAllFormes() {
        return formeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Laboratoire> findAllLaboratoires() {
        return laboratoireRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Molecule> findAllMolecules() {
        return moleculeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<UniteDosage> findAllUnitesDosage() {
        return uniteDosageRepository.findAll();
    }

    private MedicamentCompositionForm toCompositionForm(MedicamentComposition composition) {
        return new MedicamentCompositionForm(
                composition.getMolecule().getId(),
                composition.getDosageValeur(),
                composition.getUniteDosage().getId(),
                composition.getOrdreAffichage());
    }

    private void applyBaseFields(Medicament medicament, MedicamentAdminForm form) {
        medicament.setLibelle(form.getLibelle().trim());
        medicament.setLibelleComplet(form.getLibelleComplet().trim());
        medicament.setForme(resolveForme(form.getFormeId()));
        medicament.setLaboratoire(resolveLaboratoire(form.getLaboratoireId()));
        medicament.setPresentation(trimToNull(form.getPresentation()));
        medicament.setPrixPharmacie(form.getPrixPharmacie());
        medicament.setPrixGrossiste(form.getPrixGrossiste());
        medicament.setPrixCamec(form.getPrixCamec());
        medicament.setSource(trimToNull(form.getSource()));
    }

    private GroupeEquivalence resolveGroupeEquivalence(MedicamentAdminForm form) {
        String signature = equivalenceSignatureService.generateSignature(form.getFormeId(), form.getCompositions());
        return groupeEquivalenceRepository.findBySignature(signature)
                .orElseGet(() -> {
                    GroupeEquivalence newGroup = new GroupeEquivalence();
                    newGroup.setSignature(signature);
                    return groupeEquivalenceRepository.save(newGroup);
                });
    }

    private void replaceCompositions(Medicament medicament, List<MedicamentCompositionForm> compositionForms) {
        medicamentCompositionRepository.deleteByMedicament_Id(medicament.getId());

        List<MedicamentComposition> compositions = compositionForms.stream()
                .sorted(Comparator
                        .comparing(MedicamentCompositionForm::getOrdreAffichage,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(MedicamentCompositionForm::getMoleculeId)
                        .thenComparing(form -> normalizeDecimal(form.getDosageValeur()))
                        .thenComparing(MedicamentCompositionForm::getUniteDosageId))
                .map(form -> toCompositionEntity(medicament, form))
                .toList();

        medicamentCompositionRepository.saveAll(compositions);
    }

    private MedicamentComposition toCompositionEntity(Medicament medicament, MedicamentCompositionForm form) {
        MedicamentComposition composition = new MedicamentComposition();
        composition.setMedicament(medicament);
        composition.setMolecule(resolveMolecule(form.getMoleculeId()));
        composition.setDosageValeur(form.getDosageValeur().stripTrailingZeros());
        composition.setUniteDosage(resolveUniteDosage(form.getUniteDosageId()));
        composition.setOrdreAffichage(form.getOrdreAffichage());
        return composition;
    }

    private Forme resolveForme(Long formeId) {
        if (formeId == null) {
            return null;
        }
        return formeRepository.findById(formeId)
                .orElseThrow(() -> new ResourceNotFoundException("Forme not found with id: " + formeId));
    }

    private Laboratoire resolveLaboratoire(Long laboratoireId) {
        return laboratoireRepository.findById(laboratoireId)
                .orElseThrow(() -> new ResourceNotFoundException("Laboratoire not found with id: " + laboratoireId));
    }

    private Molecule resolveMolecule(Long moleculeId) {
        return moleculeRepository.findById(moleculeId)
                .orElseThrow(() -> new ResourceNotFoundException("Molecule not found with id: " + moleculeId));
    }

    private UniteDosage resolveUniteDosage(Long uniteDosageId) {
        return uniteDosageRepository.findById(uniteDosageId)
                .orElseThrow(() -> new ResourceNotFoundException("UniteDosage not found with id: " + uniteDosageId));
    }

    private void validateForm(MedicamentAdminForm form) {
        if (form == null) {
            throw new IllegalArgumentException("Le formulaire du medicament est obligatoire.");
        }
        if (isBlank(form.getLibelle())) {
            throw new IllegalArgumentException("Le libelle est obligatoire.");
        }
        if (isBlank(form.getLibelleComplet())) {
            throw new IllegalArgumentException("Le libelle complet est obligatoire.");
        }
        if (form.getLaboratoireId() == null) {
            throw new IllegalArgumentException("Le laboratoire est obligatoire.");
        }

        validatePrice("prix pharmacie", form.getPrixPharmacie());
        validatePrice("prix grossiste", form.getPrixGrossiste());
        validatePrice("prix camec", form.getPrixCamec());

        List<MedicamentCompositionForm> compositions = form.getCompositions();
        if (compositions == null || compositions.isEmpty()) {
            throw new IllegalArgumentException("Le medicament doit contenir au moins une composition.");
        }

        Set<String> uniqueLines = new HashSet<>();
        for (MedicamentCompositionForm line : compositions) {
            if (line == null) {
                throw new IllegalArgumentException("Une ligne de composition est invalide.");
            }
            if (line.getMoleculeId() == null) {
                throw new IllegalArgumentException("La molecule est obligatoire pour chaque composition.");
            }
            if (line.getUniteDosageId() == null) {
                throw new IllegalArgumentException("L'unite de dosage est obligatoire pour chaque composition.");
            }
            if (line.getDosageValeur() == null || line.getDosageValeur().compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Le dosage doit etre strictement positif pour chaque composition.");
            }

            String duplicateKey = line.getMoleculeId() + ":" + normalizeDecimal(line.getDosageValeur()) + ":" + line.getUniteDosageId();
            if (!uniqueLines.add(duplicateKey)) {
                throw new IllegalArgumentException("Les lignes de composition doivent etre uniques.");
            }
        }
    }

    private void validatePrice(String fieldName, BigDecimal price) {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le " + fieldName + " ne peut pas etre negatif.");
        }
    }

    private String normalizeDecimal(BigDecimal value) {
        BigDecimal normalized = Objects.requireNonNull(value).stripTrailingZeros();
        return normalized.toPlainString();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}


