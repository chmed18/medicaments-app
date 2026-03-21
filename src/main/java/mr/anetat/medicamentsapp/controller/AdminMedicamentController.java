package mr.anetat.medicamentsapp.controller;

import mr.anetat.medicamentsapp.dto.MedicamentAdminForm;
import mr.anetat.medicamentsapp.dto.MedicamentAdminListItemDto;
import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import mr.anetat.medicamentsapp.exception.ResourceNotFoundException;
import mr.anetat.medicamentsapp.service.MedicamentAdminService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
@RequestMapping("/admin/medicaments")
public class AdminMedicamentController {

    private static final int DEFAULT_PAGE_SIZE = 10;

    private final MedicamentAdminService medicamentAdminService;

    public AdminMedicamentController(MedicamentAdminService medicamentAdminService) {
        this.medicamentAdminService = medicamentAdminService;
    }

    @GetMapping
    public String list(
            @RequestParam(name = "q", required = false) String query,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(Math.max(page, 0), clampPageSize(size));
        Page<MedicamentAdminListItemDto> resultPage = medicamentAdminService.search(query, pageable);
        model.addAttribute("medicaments", resultPage.getContent());
        model.addAttribute("page", resultPage);
        model.addAttribute("query", query == null ? "" : query.trim());
        return "admin/medicaments/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        MedicamentAdminForm form = new MedicamentAdminForm();
        form.getCompositions().add(new MedicamentCompositionForm());
        model.addAttribute("form", form);
        model.addAttribute("isEdit", false);
        addReferenceData(model);
        return "admin/medicaments/form";
    }

    @PostMapping
    public String create(
            @Valid @ModelAttribute("form") MedicamentAdminForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("isEdit", false);
            addReferenceData(model);
            return "admin/medicaments/form";
        }

        try {
            medicamentAdminService.create(form);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament créé avec succès.");
            return "redirect:/admin/medicaments";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("business", ex.getMessage());
            model.addAttribute("isEdit", false);
            addReferenceData(model);
            return "admin/medicaments/form";
        } catch (ResourceNotFoundException ex) {
            bindingResult.reject("notFound", ex.getMessage());
            model.addAttribute("isEdit", false);
            addReferenceData(model);
            return "admin/medicaments/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("form", medicamentAdminService.getFormById(id));
            model.addAttribute("medicamentId", id);
            model.addAttribute("isEdit", true);
            addReferenceData(model);
            return "admin/medicaments/form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/medicaments";
        }
    }

    @PostMapping("/{id}")
    public String update(
            @PathVariable Long id,
            @Valid @ModelAttribute("form") MedicamentAdminForm form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("medicamentId", id);
            model.addAttribute("isEdit", true);
            addReferenceData(model);
            return "admin/medicaments/form";
        }

        try {
            medicamentAdminService.update(id, form);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament mis à jour avec succès.");
            return "redirect:/admin/medicaments";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("business", ex.getMessage());
            model.addAttribute("medicamentId", id);
            model.addAttribute("isEdit", true);
            addReferenceData(model);
            return "admin/medicaments/form";
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
            return "redirect:/admin/medicaments";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            medicamentAdminService.delete(id);
            redirectAttributes.addFlashAttribute("successMessage", "Médicament supprimé avec succès.");
        } catch (ResourceNotFoundException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/admin/medicaments";
    }

    private void addReferenceData(Model model) {
        model.addAttribute("formes", medicamentAdminService.findAllFormes());
        model.addAttribute("laboratoires", medicamentAdminService.findAllLaboratoires());
        model.addAttribute("molecules", medicamentAdminService.findAllMolecules());
        model.addAttribute("unitesDosage", medicamentAdminService.findAllUnitesDosage());
    }

    private int clampPageSize(int size) {
        if (size <= 0) {
            return DEFAULT_PAGE_SIZE;
        }
        return Math.min(size, 100);
    }
}



