package mr.anetat.medicamentsapp.controller;

import mr.anetat.medicamentsapp.dto.MedicamentAdminForm;
import mr.anetat.medicamentsapp.dto.MedicamentCompositionForm;
import mr.anetat.medicamentsapp.service.MedicamentAdminService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/medicaments")
public class AdminMedicamentController {

    private final MedicamentAdminService medicamentAdminService;

    public AdminMedicamentController(MedicamentAdminService medicamentAdminService) {
        this.medicamentAdminService = medicamentAdminService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("medicaments", medicamentAdminService.findAllForAdmin());
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
            redirectAttributes.addFlashAttribute("successMessage", "Medicament cree avec succes.");
            return "redirect:/admin/medicaments";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("business", ex.getMessage());
            model.addAttribute("isEdit", false);
            addReferenceData(model);
            return "admin/medicaments/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model) {
        model.addAttribute("form", medicamentAdminService.getFormById(id));
        model.addAttribute("medicamentId", id);
        model.addAttribute("isEdit", true);
        addReferenceData(model);
        return "admin/medicaments/form";
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
            redirectAttributes.addFlashAttribute("successMessage", "Medicament mis a jour avec succes.");
            return "redirect:/admin/medicaments";
        } catch (IllegalArgumentException ex) {
            bindingResult.reject("business", ex.getMessage());
            model.addAttribute("medicamentId", id);
            model.addAttribute("isEdit", true);
            addReferenceData(model);
            return "admin/medicaments/form";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        medicamentAdminService.delete(id);
        redirectAttributes.addFlashAttribute("successMessage", "Medicament supprime avec succes.");
        return "redirect:/admin/medicaments";
    }

    private void addReferenceData(Model model) {
        model.addAttribute("formes", medicamentAdminService.findAllFormes());
        model.addAttribute("laboratoires", medicamentAdminService.findAllLaboratoires());
        model.addAttribute("molecules", medicamentAdminService.findAllMolecules());
        model.addAttribute("unitesDosage", medicamentAdminService.findAllUnitesDosage());
    }
}


