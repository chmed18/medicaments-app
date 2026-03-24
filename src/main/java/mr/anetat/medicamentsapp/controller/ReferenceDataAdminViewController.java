package mr.anetat.medicamentsapp.controller;

import mr.anetat.medicamentsapp.domain.Forme;
import mr.anetat.medicamentsapp.domain.Laboratoire;
import mr.anetat.medicamentsapp.domain.Molecule;
import mr.anetat.medicamentsapp.domain.UniteDosage;
import mr.anetat.medicamentsapp.dto.ReferenceDataAdminForm;
import mr.anetat.medicamentsapp.dto.ReferenceDataRowDto;
import mr.anetat.medicamentsapp.exception.ReferenceDataDuplicateException;
import mr.anetat.medicamentsapp.exception.ReferenceDataInUseException;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.service.FormeService;
import mr.anetat.medicamentsapp.service.LaboratoireService;
import mr.anetat.medicamentsapp.service.MoleculeService;
import mr.anetat.medicamentsapp.service.UniteDosageService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/referentiels")
public class ReferenceDataAdminViewController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final FormeService formeService;
    private final LaboratoireService laboratoireService;
    private final MoleculeService moleculeService;
    private final UniteDosageService uniteDosageService;

    public ReferenceDataAdminViewController(
            FormeService formeService,
            LaboratoireService laboratoireService,
            MoleculeService moleculeService,
            UniteDosageService uniteDosageService) {
        this.formeService = formeService;
        this.laboratoireService = laboratoireService;
        this.moleculeService = moleculeService;
        this.uniteDosageService = uniteDosageService;
    }


    @GetMapping
    public String index() {
        return "redirect:/admin/referentiels/laboratoires";
    }

    @GetMapping("/{type}")
    public String list(
            @PathVariable String type,
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        ReferenceDataType resolvedType = ReferenceDataType.fromSlug(type);
        String sortField = switch (resolvedType) {
            case LABORATOIRES, MOLECULES -> "nom";
            case FORMES, UNITES_DOSAGE -> "libelle";
        };
        Pageable pageable = PageRequest.of(Math.max(page, 0), clampPageSize(size), Sort.by(Sort.Direction.ASC, sortField));

        Page<?> resultPage = switch (resolvedType) {
            case FORMES -> formeService.search(query, pageable);
            case LABORATOIRES -> laboratoireService.search(query, pageable);
            case MOLECULES -> moleculeService.search(query, pageable);
            case UNITES_DOSAGE -> uniteDosageService.search(query, pageable);
        };

        Page<ReferenceDataRowDto> viewPage = resultPage.map(item -> switch (resolvedType) {
            case FORMES -> {
                Forme forme = (Forme) item;
                yield new ReferenceDataRowDto(forme.getId(), forme.getLibelle(), forme.getLibelleComplet());
            }
            case LABORATOIRES -> {
                Laboratoire laboratoire = (Laboratoire) item;
                yield new ReferenceDataRowDto(laboratoire.getId(), laboratoire.getNom(), laboratoire.getAdresse());
            }
            case MOLECULES -> {
                Molecule molecule = (Molecule) item;
                yield new ReferenceDataRowDto(molecule.getId(), molecule.getNom(), null);
            }
            case UNITES_DOSAGE -> {
                UniteDosage uniteDosage = (UniteDosage) item;
                yield new ReferenceDataRowDto(uniteDosage.getId(), uniteDosage.getLibelle(), uniteDosage.getLibelleComplet());
            }
        });

        model.addAttribute("type", resolvedType);
        model.addAttribute("items", viewPage.getContent());
        model.addAttribute("page", viewPage);
        model.addAttribute("query", query == null ? "" : query.trim());
        return "admin/referentiels/list";
    }

    @GetMapping("/{type}/new")
    public String createForm(@PathVariable String type, Model model) {
        ReferenceDataType resolvedType = ReferenceDataType.fromSlug(type);
        model.addAttribute("type", resolvedType);
        model.addAttribute("form", new ReferenceDataAdminForm());
        model.addAttribute("isEdit", false);
        return "admin/referentiels/form";
    }

    @PostMapping("/{type}")
    public String create(
            @PathVariable String type,
            @Valid @ModelAttribute("form") ReferenceDataAdminForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        ReferenceDataType resolvedType = ReferenceDataType.fromSlug(type);
        if (bindingResult.hasErrors()) {
            model.addAttribute("type", resolvedType);
            model.addAttribute("form", form);
            model.addAttribute("isEdit", false);
            return "admin/referentiels/form";
        }

        try {
            switch (resolvedType) {
                case FORMES -> formeService.create(toForme(form));
                case LABORATOIRES -> laboratoireService.create(toLaboratoire(form));
                case MOLECULES -> moleculeService.create(toMolecule(form));
                case UNITES_DOSAGE -> uniteDosageService.create(toUniteDosage(form));
            }
            redirectAttributes.addFlashAttribute("successMessage", successCreateMessage(resolvedType));
            return redirectToList(resolvedType);
        } catch (ReferenceDataDuplicateException ex) {
            bindingResult.rejectValue("primaryValue", "duplicate", ex.getMessage());
            model.addAttribute("type", resolvedType);
            model.addAttribute("form", form);
            model.addAttribute("isEdit", false);
            return "admin/referentiels/form";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("primaryValue", "invalid", ex.getMessage());
            model.addAttribute("type", resolvedType);
            model.addAttribute("form", form);
            model.addAttribute("isEdit", false);
            return "admin/referentiels/form";
        }
    }

    @GetMapping("/{type}/{id}/edit")
    public String editForm(@PathVariable String type, @PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        ReferenceDataType resolvedType = ReferenceDataType.fromSlug(type);
        try {
            ReferenceDataAdminForm form = switch (resolvedType) {
                case FORMES -> fromForme(formeService.findById(id));
                case LABORATOIRES -> fromLaboratoire(laboratoireService.findById(id));
                case MOLECULES -> fromMolecule(moleculeService.findById(id));
                case UNITES_DOSAGE -> fromUniteDosage(uniteDosageService.findById(id));
            };
            model.addAttribute("type", resolvedType);
            model.addAttribute("form", form);
            model.addAttribute("entityId", id);
            model.addAttribute("isEdit", true);
            return "admin/referentiels/form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return redirectToList(resolvedType);
        }
    }

    @PostMapping("/{type}/{id}")
    public String update(
            @PathVariable String type,
            @PathVariable Long id,
            @Valid @ModelAttribute("form") ReferenceDataAdminForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        ReferenceDataType resolvedType = ReferenceDataType.fromSlug(type);
        if (bindingResult.hasErrors()) {
            model.addAttribute("type", resolvedType);
            model.addAttribute("entityId", id);
            model.addAttribute("isEdit", true);
            return "admin/referentiels/form";
        }

        try {
            switch (resolvedType) {
                case FORMES -> formeService.update(id, toForme(form));
                case LABORATOIRES -> laboratoireService.update(id, toLaboratoire(form));
                case MOLECULES -> moleculeService.update(id, toMolecule(form));
                case UNITES_DOSAGE -> uniteDosageService.update(id, toUniteDosage(form));
            }
            redirectAttributes.addFlashAttribute("successMessage", successUpdateMessage(resolvedType));
            return redirectToList(resolvedType);
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return redirectToList(resolvedType);
        } catch (ReferenceDataDuplicateException ex) {
            bindingResult.rejectValue("primaryValue", "duplicate", ex.getMessage());
            model.addAttribute("type", resolvedType);
            model.addAttribute("entityId", id);
            model.addAttribute("isEdit", true);
            return "admin/referentiels/form";
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("primaryValue", "invalid", ex.getMessage());
            model.addAttribute("type", resolvedType);
            model.addAttribute("entityId", id);
            model.addAttribute("isEdit", true);
            return "admin/referentiels/form";
        }
    }

    @PostMapping("/{type}/{id}/delete")
    public String delete(
            @PathVariable String type,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        ReferenceDataType resolvedType = ReferenceDataType.fromSlug(type);
        try {
            switch (resolvedType) {
                case FORMES -> formeService.delete(id);
                case LABORATOIRES -> laboratoireService.delete(id);
                case MOLECULES -> moleculeService.delete(id);
                case UNITES_DOSAGE -> uniteDosageService.delete(id);
            }
            redirectAttributes.addFlashAttribute("successMessage", successDeleteMessage(resolvedType));
        } catch (ResourceNotFoundException | ReferenceDataInUseException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return redirectToList(resolvedType);
    }

    private String redirectToList(ReferenceDataType type) {
        return "redirect:/admin/referentiels/" + type.getSlug();
    }

    private String successCreateMessage(ReferenceDataType type) {
        return switch (type) {
            case FORMES -> "Forme créée avec succès.";
            case LABORATOIRES -> "Laboratoire créé avec succès.";
            case MOLECULES -> "Molécule créée avec succès.";
            case UNITES_DOSAGE -> "Unité de dosage créée avec succès.";
        };
    }

    private String successUpdateMessage(ReferenceDataType type) {
        return switch (type) {
            case FORMES -> "Forme mise à jour avec succès.";
            case LABORATOIRES -> "Laboratoire mis à jour avec succès.";
            case MOLECULES -> "Molécule mise à jour avec succès.";
            case UNITES_DOSAGE -> "Unité de dosage mise à jour avec succès.";
        };
    }

    private String successDeleteMessage(ReferenceDataType type) {
        return switch (type) {
            case FORMES -> "Forme supprimée avec succès.";
            case LABORATOIRES -> "Laboratoire supprimé avec succès.";
            case MOLECULES -> "Molécule supprimée avec succès.";
            case UNITES_DOSAGE -> "Unité de dosage supprimée avec succès.";
        };
    }

    private int clampPageSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, 100);
    }

    private ReferenceDataAdminForm fromForme(Forme forme) {
        ReferenceDataAdminForm form = new ReferenceDataAdminForm();
        form.setPrimaryValue(forme.getLibelle());
        form.setSecondaryValue(forme.getLibelleComplet());
        return form;
    }

    private ReferenceDataAdminForm fromLaboratoire(Laboratoire laboratoire) {
        ReferenceDataAdminForm form = new ReferenceDataAdminForm();
        form.setPrimaryValue(laboratoire.getNom());
        form.setSecondaryValue(laboratoire.getAdresse());
        return form;
    }

    private ReferenceDataAdminForm fromMolecule(Molecule molecule) {
        ReferenceDataAdminForm form = new ReferenceDataAdminForm();
        form.setPrimaryValue(molecule.getNom());
        return form;
    }

    private ReferenceDataAdminForm fromUniteDosage(UniteDosage uniteDosage) {
        ReferenceDataAdminForm form = new ReferenceDataAdminForm();
        form.setPrimaryValue(uniteDosage.getLibelle());
        form.setSecondaryValue(uniteDosage.getLibelleComplet());
        return form;
    }

    private Forme toForme(ReferenceDataAdminForm form) {
        Forme entity = new Forme();
        entity.setLibelle(form.getPrimaryValue().trim());
        entity.setLibelleComplet(trimToNull(form.getSecondaryValue()));
        return entity;
    }

    private Laboratoire toLaboratoire(ReferenceDataAdminForm form) {
        Laboratoire entity = new Laboratoire();
        entity.setNom(form.getPrimaryValue().trim());
        entity.setAdresse(trimToNull(form.getSecondaryValue()));
        return entity;
    }

    private Molecule toMolecule(ReferenceDataAdminForm form) {
        Molecule entity = new Molecule();
        entity.setNom(form.getPrimaryValue().trim());
        return entity;
    }

    private UniteDosage toUniteDosage(ReferenceDataAdminForm form) {
        UniteDosage entity = new UniteDosage();
        entity.setLibelle(form.getPrimaryValue().trim());
        entity.setLibelleComplet(trimToNull(form.getSecondaryValue()));
        return entity;
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
